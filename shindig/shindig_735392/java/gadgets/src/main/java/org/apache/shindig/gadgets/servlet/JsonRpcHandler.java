/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets.servlet;

import org.apache.shindig.gadgets.Gadget;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.UrlGenerator;
import org.apache.shindig.gadgets.process.ProcessingException;
import org.apache.shindig.gadgets.process.Processor;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.LinkSpec;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.apache.shindig.gadgets.spec.UserPref;
import org.apache.shindig.gadgets.spec.View;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * Processes JSON-RPC requests by retrieving all necessary meta data in parallel and coalescing into
 * a single output JSON construct.
 */
public class JsonRpcHandler {
  private final ExecutorService executor;
  private final Processor processor;
  private final UrlGenerator urlGenerator;

  @Inject
  public JsonRpcHandler(ExecutorService executor, Processor processor, UrlGenerator urlGenerator) {
    this.executor = executor;
    this.processor = processor;
    this.urlGenerator = urlGenerator;
  }

  /**
   * Processes a JSON request.
   *
   * @param request Original JSON request
   * @return The JSON response.
   */
  public JSONObject process(JSONObject request) throws RpcException, JSONException {
    List<GadgetContext> gadgets;

    JSONObject requestContext = request.getJSONObject("context");
    JSONArray requestedGadgets = request.getJSONArray("gadgets");

    // Process all JSON first so that we don't wind up with hanging threads if
    // a JSONException is thrown.
    gadgets = Lists.newArrayListWithExpectedSize(requestedGadgets.length());
    for (int i = 0, j = requestedGadgets.length(); i < j; ++i) {
      GadgetContext context = new JsonRpcGadgetContext(
          requestContext, requestedGadgets.getJSONObject(i));
      gadgets.add(context);
    }

    // Dispatch a separate thread for each gadget that we wish to render.
    // We could probably just submit these directly to the ExecutorService, but if it's an async
    // service instead of a threaded one we would just block.
    CompletionService<JSONObject> processor =  new ExecutorCompletionService<JSONObject>(executor);

    for (GadgetContext context : gadgets) {
      processor.submit(new Job(context));
    }

    JSONObject response = new JSONObject();

    int numJobs = gadgets.size();
    do {
      try {
        JSONObject gadget = processor.take().get();
        response.append("gadgets", gadget);
      } catch (InterruptedException e) {
        throw new RpcException("Processing interrupted", e);
      } catch (ExecutionException ee) {
        if (!(ee.getCause() instanceof RpcException)) {
          throw new RpcException("Processing interrupted", ee);
        }
        RpcException e = (RpcException)ee.getCause();
        // Just one gadget failed; mark it as such.
        try {
          GadgetContext context = e.getContext();
          JSONObject errorObj = new JSONObject();
          errorObj.put("url", context.getUrl())
                  .put("moduleId", context.getModuleId());
          errorObj.append("errors", e.getCause().getLocalizedMessage());
          response.append("gadgets", errorObj);
        } catch (JSONException je) {
          throw new RpcException("Unable to write JSON", je);
        }
      } catch (JSONException e) {
        throw new RpcException("Unable to write JSON", e);
      } finally {
        numJobs--;
      }
    } while (numJobs > 0);
    return response;
  }

  private class Job implements Callable<JSONObject> {
    private final GadgetContext context;

    public Job(GadgetContext context) {
      this.context = context;
    }

    public JSONObject call() throws RpcException {
      try {
        Gadget gadget = processor.process(context);
        GadgetSpec spec = gadget.getSpec();

        JSONObject gadgetJson = new JSONObject();

        ModulePrefs prefs = spec.getModulePrefs();

        // TODO: modularize response fields based on requested items.
        JSONObject views = new JSONObject();
        for (View view : spec.getViews().values()) {
          views.put(view.getName(), new JSONObject()
               // .put("content", view.getContent())
               .put("type", view.getType().toString().toLowerCase())
               .put("quirks", view.getQuirks())
               .put("preferredHeight", view.getPreferredHeight())
               .put("preferredWidth", view.getPreferredWidth()));
        }

        // Features.
        Set<String> feats = prefs.getFeatures().keySet();
        String[] features = feats.toArray(new String[feats.size()]);

        // Links
        JSONObject links = new JSONObject();
        for (LinkSpec link : prefs.getLinks().values()) {
          links.put(link.getRel(), link.getHref());
        }

        JSONObject userPrefs = new JSONObject();

        // User pref specs
        for (UserPref pref : spec.getUserPrefs()) {
          JSONObject up = new JSONObject()
              .put("displayName", pref.getDisplayName())
              .put("type", pref.getDataType().toString().toLowerCase())
              .put("default", pref.getDefaultValue())
              .put("enumValues", pref.getEnumValues())
              .put("orderedEnumValues", getOrderedEnums(pref));
          userPrefs.put(pref.getName(), up);
        }

        // TODO: This should probably just copy all data from
        // ModulePrefs.getAttributes(), but names have to be converted to
        // camel case.
        gadgetJson.put("iframeUrl", urlGenerator.getIframeUrl(gadget))
                  .put("url",context.getUrl().toString())
                  .put("moduleId", context.getModuleId())
                  .put("title", prefs.getTitle())
                  .put("titleUrl", prefs.getTitleUrl().toString())
                  .put("views", views)
                  .put("features", features)
                  .put("userPrefs", userPrefs)
                  .put("links", links)

                  // extended meta data
                  .put("directoryTitle", prefs.getDirectoryTitle())
                  .put("thumbnail", prefs.getThumbnail().toString())
                  .put("screenshot", prefs.getScreenshot().toString())
                  .put("author", prefs.getAuthor())
                  .put("authorEmail", prefs.getAuthorEmail())
                  .put("authorAffiliation", prefs.getAuthorAffiliation())
                  .put("authorLocation", prefs.getAuthorLocation())
                  .put("authorPhoto", prefs.getAuthorPhoto())
                  .put("authorAboutme", prefs.getAuthorAboutme())
                  .put("authorQuote", prefs.getAuthorQuote())
                  .put("authorLink", prefs.getAuthorLink())
                  .put("categories", prefs.getCategories())
                  .put("screenshot", prefs.getScreenshot().toString())
                  .put("height", prefs.getHeight())
                  .put("width", prefs.getWidth())
                  .put("showStats", prefs.getShowStats())
                  .put("showInDirectory", prefs.getShowInDirectory())
                  .put("singleton", prefs.getSingleton())
                  .put("scaling", prefs.getScaling())
                  .put("scrolling", prefs.getScrolling());
        return gadgetJson;
      } catch (ProcessingException e) {
        throw new RpcException(context, e);
      } catch (JSONException e) {
        // Shouldn't be possible
        throw new RpcException(context, e);
      }
    }

    private List<JSONObject> getOrderedEnums(UserPref pref) throws JSONException {
      List<UserPref.EnumValuePair> orderedEnums = pref.getOrderedEnumValues();
      List<JSONObject> jsonEnums = Lists.newArrayListWithExpectedSize(orderedEnums.size());
      for (UserPref.EnumValuePair evp : orderedEnums) {
        JSONObject curEnum = new JSONObject();
        curEnum.put("value", evp.getValue());
        curEnum.put("displayValue", evp.getDisplayValue());
        jsonEnums.add(curEnum);
      }
      return jsonEnums;
    }
  }
}
