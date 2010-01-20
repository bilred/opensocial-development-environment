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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefModel;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefModel.DataType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MetaDataFetcher {
	
	private static final String METADATA_POST =
		"{\"context\":{\"country\":\"$country$\",\"language\":\"$language$\",\"view\":\"$view$\","
		+ "\"container\":\"default\"},\"gadgets\":[{\"url\":\"$url$\",\"moduleId\":1}]}";

	public static List<UserPrefModel> fetch(
			String view, String viewer, String owner,String appId,
			String country, String language, String url) throws Exception {
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		client.getHostConfiguration().setHost("localhost", 8080, "http");
		PostMethod post = new PostMethod("/gadgets/metadata");
		String body = METADATA_POST.replace("$country$", country);
		body = body.replace("$language$", language);
		body = body.replace("$view$", view);
		body = body.replace("$url$", url);
		post.setRequestEntity(new StringRequestEntity(body, "text/json", "UTF-8"));
		client.executeMethod(post);
		//
		JSONParser parser = new JSONParser();
		JSONObject metadata = (JSONObject)parser.parse(new StringReader(post.getResponseBodyAsString()));
		JSONArray gadgets = (JSONArray)metadata.get("gadgets");
		JSONObject gadget = (JSONObject)gadgets.get(0);
		if (gadget.containsKey("errors")) {
			JSONArray errors = (JSONArray)gadget.get("errors");
			String errorMsg = "";
			for (Object error : errors) {
				errorMsg += error.toString();
			}
			throw new IOException(errorMsg);
		} else {
			JSONObject userPrefs = (JSONObject)gadget.get("userPrefs");
			//
			List<UserPrefModel> resultModels = new ArrayList<UserPrefModel>();
			for (Object name : userPrefs.keySet()) {
				JSONObject userPref = (JSONObject)userPrefs.get(name);
				UserPrefModel model = new UserPrefModel();
				model.setName(name.toString());
				model.setDisplayName(userPref.get("displayName").toString());
				model.setDefaultValue(userPref.get("default").toString());
				model.setDataType(DataType.getDataType(userPref.get("type").toString()));
				Map<String, String> enumValueMap = model.getEnumValueMap();
				JSONObject enumValues = (JSONObject)userPref.get("enumValues");
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
