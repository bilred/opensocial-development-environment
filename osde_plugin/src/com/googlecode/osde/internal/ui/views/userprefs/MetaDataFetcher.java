/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.ui.views.userprefs;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.googlecode.osde.internal.editors.pref.UserPrefModel;
import com.googlecode.osde.internal.editors.pref.UserPrefModel.DataType;
import com.googlecode.osde.internal.shindig.ShindigServer;

public class MetaDataFetcher {

    private static final String METADATA_POST =
            "{\"context\":{\"country\":\"$country$\",\"language\":\"$language$\",\"view\":\"$view$\","
                    + "\"container\":\"default\"},\"gadgets\":[{\"url\":\"$url$\",\"moduleId\":1}]}";

    public static List<UserPrefModel> fetch(
            String view, String appId, String country, String language, String url) throws Exception {

        HttpClient client = new DefaultHttpClient();
        String body = METADATA_POST.replace("$country$", country);
        body = body.replace("$language$", language);
        body = body.replace("$view$", view);
        body = body.replace("$url$", url);
        HttpPost post = new HttpPost(
        		"http://localhost:" + ShindigServer.DEFAULT_SHINDIG_PORT + "/gadgets/metadata");
        post.setHeader("Content-Type", "text/json");
        post.setEntity(new StringEntity(body, "UTF-8"));
        String response = client.execute(post, new BasicResponseHandler());
        //
        JSONParser parser = new JSONParser();
        JSONObject metadata = (JSONObject) parser.parse(new StringReader(response));
        JSONArray gadgets = (JSONArray) metadata.get("gadgets");
        JSONObject gadget = (JSONObject) gadgets.get(0);
        if (gadget.containsKey("errors")) {
            JSONArray errors = (JSONArray) gadget.get("errors");
            String errorMsg = "";
            for (Object error : errors) {
                errorMsg += error.toString();
            }
            throw new IOException(errorMsg);
        } else {
            JSONObject userPrefs = (JSONObject) gadget.get("userPrefs");
            //
            List<UserPrefModel> resultModels = new ArrayList<UserPrefModel>();
            for (Object name : userPrefs.keySet()) {
                JSONObject userPref = (JSONObject) userPrefs.get(name);
                UserPrefModel model = new UserPrefModel();
                model.setName(name.toString());
                model.setDisplayName(userPref.get("displayName").toString());
                model.setDefaultValue(userPref.get("default").toString());
                model.setDataType(DataType.getDataType(userPref.get("type").toString()));
                Map<String, String> enumValueMap = model.getEnumValueMap();
                JSONObject enumValues = (JSONObject) userPref.get("enumValues");
                for (Object value : enumValues.keySet()) {
                    enumValueMap.put(value.toString(), enumValues.get(value).toString());
                }
                resultModels.add(model);
            }
            //
            return resultModels;
        }
    }

}
