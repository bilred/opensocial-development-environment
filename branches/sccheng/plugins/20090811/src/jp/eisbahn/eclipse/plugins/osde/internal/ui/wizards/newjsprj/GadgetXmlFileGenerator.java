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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newjsprj;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Set;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;

public class GadgetXmlFileGenerator {
	
	private IProject project;
	
	private GadgetXmlData gadgetXmlData;
	
	private EnumMap<ViewName, GadgetViewData> gadgetViewData;
	
	public GadgetXmlFileGenerator(
			IProject project, GadgetXmlData gadgetXmlData, EnumMap<ViewName, GadgetViewData> gadgetViewData) {
		super();
		this.project = project;
		this.gadgetXmlData = gadgetXmlData;
		this.gadgetViewData = gadgetViewData;
	}
	
	public IFile generate(IProgressMonitor monitor) throws UnsupportedEncodingException, CoreException {
		try {
			monitor.beginTask("Generate Gadget XML file.", 100);
			IFile gadgetXmlFile = project.getFile(new Path(gadgetXmlData.getGadgetSpecFilename()));
			String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			content += "<Module>\n";
			content += "  <ModulePrefs";
			content += " title=\"" + gadgetXmlData.getTitle() + "\"";
			if (gadgetXmlData.getTitleUrl().length() > 0) {
				content += " title_url=\"" + gadgetXmlData.getTitleUrl() + "\"";
			}
			if (gadgetXmlData.getAuthor().length() > 0) {
				content += " author=\"" + gadgetXmlData.getAuthor() + "\"";
			}
			content += " author_email=\"" + gadgetXmlData.getAuthorEmail() + "\"";
			if (gadgetXmlData.getDescription().length() > 0) {
				content += " description=\"" + gadgetXmlData.getDescription() + "\"";
			}
			if (gadgetXmlData.getScreenshot().length() > 0) {
				content += " screenshot=\"" + gadgetXmlData.getScreenshot() + "\"";
			}
			if (gadgetXmlData.getThumbnail().length() > 0) {
				content += " thumbnail=\"" + gadgetXmlData.getThumbnail() + "\"";
			}
			content += ">\n";
			if (gadgetXmlData.isOpensocial08()) {
				content += "    <Require feature=\"opensocial-0.8\" />\n";
			}
			if (gadgetXmlData.isOpensocial07()) {
				content += "    <Require feature=\"opensocial-0.7\" />\n";
			}
			if (gadgetXmlData.isDynamicHeight() || hasCreateSampleCodeSet()) {
				content += "    <Require feature=\"dynamic-height\" />\n";
			}
			if (gadgetXmlData.isFlash()) {
				content += "    <Require feature=\"flash\" />\n";
			}
			if (gadgetXmlData.isMiniMessage()) {
				content += "    <Require feature=\"minimessage\" />\n";
			}
			if (gadgetXmlData.isPubsub()) {
				content += "    <Require feature=\"pubsub\" />\n";
			}
			if (gadgetXmlData.isSetTitle()) {
				content += "    <Require feature=\"settitle\" />\n";
			}
			if (gadgetXmlData.isSkins()) {
				content += "    <Require feature=\"skins\" />\n";
			}
			if (gadgetXmlData.isTabs()) {
				content += "    <Require feature=\"tabs\" />\n";
			}
			if (gadgetXmlData.isViews()) {
				content += "    <Require feature=\"views\" />\n";
			}
			content += "  </ModulePrefs>\n";
			Set<ViewName> keySet = gadgetViewData.keySet();
			for (ViewName viewName : keySet) {
				GadgetViewData viewData = gadgetViewData.get(viewName);
				if (viewData.getType().equals(ViewType.html)) {
					content += "  <Content type=\"html\" view=\"" + viewName.toString() + "\"><![CDATA[\n";
					if (viewData.isCreateSampleCodeSet()) {
						content += "\n";
						if (viewData.isCreatePeople()) {
							content += "<!-- Fetching People and Friends -->\n";
							content += "<div>\n";
							content += "  <button onclick='fetchPeople();'>Fetch people and friends</button>\n";
							content += "  <div>\n";
							content += "    <span id='viewer'></span>\n";
							content += "    <ul id='friends'></ul>\n";
							content += "  </div>\n";
							content += "</div>\n";
							content += "<script type='text/javascript'>\n";
							content += "function fetchPeople() {\n";
							content += "  var req = opensocial.newDataRequest();\n";
							content += "  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');\n";
							content += "  var params = {};\n";
							content += "  params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.VIEWER;\n";
							content += "  params[opensocial.IdSpec.Field.GROUP_ID] = 'FRIENDS';\n";
							content += "  var idSpec = opensocial.newIdSpec(params);\n";
							content += "  req.add(req.newFetchPeopleRequest(idSpec), 'friends');\n";
							content += "  req.send(function(data) {\n";
							content += "    var viewer = data.get('viewer').getData();\n";
							content += "    document.getElementById('viewer').innerHTML = viewer.getId();\n";
							content += "    var friends = data.get('friends').getData();\n";
							content += "    document.getElementById('friends').innerHTML = '';\n";
							content += "    friends.each(function(friend) {\n";
							content += "      document.getElementById('friends').innerHTML += '<li>' + friend.getId() + '</li>';\n";
							content += "    });\n";
							content += "    gadgets.window.adjustHeight();\n";
							content += "  });\n";
							content += "}\n";
							content += "</script>\n";
							content += "\n";
						}
						if (viewData.isCreateActivity()) {
							content += "<!-- Posting activity -->\n";
							content += "<div>\n";
							content += "  <input type='text' id='title' />\n";
							content += "  <button onclick='postActivity();'>Post activity</button>\n";
							content += "  <div id='result_activity'></div>\n";
							content += "</div>\n";
							content += "<script type='text/javascript'>\n";
							content += "function postActivity() {\n";
							content += "  var params = {};\n";
							content += "  params[opensocial.Activity.Field.TITLE] = document.getElementById('title').value;\n";
							content += "  var activity = opensocial.newActivity(params);\n";
							content += "  opensocial.requestCreateActivity(\n";
							content += "      activity, opensocial.CreateActivityPriority.HIGH, function(response) {\n";
							content += "        if (response.hadError()) {\n";
							content += "          document.getElementById('result_activity').innerHTML = response.getErrorMessage();\n";
							content += "        } else {\n";
							content += "          document.getElementById('result_activity').innerHTML = 'Succeeded!';\n";
							content += "        }\n";
							content += "        gadgets.window.adjustHeight();\n";
							content += "      });\n";
							content += "}\n";
							content += "</script>\n";
							content += "\n";
						}
						if (viewData.isCreateAppData()) {
							content += "<!-- Sharing data with friends -->\n";
							content += "<div>\n";
							content += "  <input type='text' id='content' />\n";
							content += "  <button onclick='shareData();'>Share data</button>\n";
							content += "  <button onclick='fetchFriendData();'>Fetch friend's data</button>\n";
							content += "  <div id='result_appdata'></div>\n";
							content += "  <ul id='contents'></ul>\n";
							content += "</div>\n";
							content += "<script type='text/javascript'>\n";
							content += "function shareData() {\n";
							content += "  var content = document.getElementById('content').value;\n";
							content += "  var req = opensocial.newDataRequest();\n";
							content += "  req.add(req.newUpdatePersonAppDataRequest(opensocial.IdSpec.PersonId.VIEWER, 'content', content));\n";
							content += "  req.send(function(response) {\n";
							content += "    if (response.hadError()) {\n";
							content += "      document.getElementById('result_appdata').innerHTML = response.getErrorMessage();\n";
							content += "    } else {\n";
							content += "      document.getElementById('result_appdata').innerHTML = 'Succeeded!';\n";
							content += "    }\n";
							content += "    gadgets.window.adjustHeight();\n";
							content += "  });\n";
							content += "}\n";
							content += "function fetchFriendData() {\n";
							content += "  var req = opensocial.newDataRequest();\n";
							content += "  var params = {};\n";
							content += "  params[opensocial.IdSpec.Field.USER_ID] = opensocial.IdSpec.PersonId.VIEWER;\n";
							content += "  params[opensocial.IdSpec.Field.GROUP_ID] = 'FRIENDS';\n";
							content += "  var idSpec = opensocial.newIdSpec(params);\n";
							content += "  req.add(req.newFetchPersonAppDataRequest(idSpec, ['content']), 'stored');\n";
							content += "  req.send(function(data) {\n";
							content += "    var stored = data.get('stored').getData();\n";
							content += "    for(var id in stored) {\n";
							content += "      var obj = stored[id];\n";
							content += "      document.getElementById('contents').innerHTML\n";
							content += "          += '<li>' + id + ': ' + obj['content'] + '</li>';\n";
							content += "    }\n";
							content += "    gadgets.window.adjustHeight();\n";
							content += "  });\n";
							content += "}\n";
							content += "</script>\n";
							content += "\n";
						}
					} else {
						if (viewData.isCreateExternalJavaScript()) {
							content += "\n";
							content += "<script type=\"text/javascript\" src=\"http://localhost:8080/" + project.getName() + "/" + viewData.getFilename() + "\"></script>\n";
							if (viewData.isCreateInitFunction()) {
								content += "\n";
								content += "<script type=\"text/javascript\">\n";
								content += "gadgets.util.registerOnLoadHandler(init);\n";
								content += "</script>\n";
							}
						}
						content += "\n";
						content += "<!-- The code for " + viewName.getDisplayName() + " view is here. -->\n";
						content += "<div>" + viewName.getDisplayName() + " view for " + gadgetXmlData.getTitle() + ".</div>\n";
						content += "\n";
					}
					content += "  ]]></Content>\n";
				} else if (viewData.getType().equals(ViewType.url)) {
					content += "  <Content type=\"url\" view=\"" + viewName.toString() + "\" href=\"" + viewData.getHref() + "\" />\n";
				}
			}
			content += "</Module>";
			monitor.worked(30);
			InputStream in = new ByteArrayInputStream(content.getBytes("UTF8"));
			monitor.worked(20);
			gadgetXmlFile.create(in, false, monitor);
			monitor.worked(50);
			return gadgetXmlFile;
		} finally {
			monitor.done();
		}
	}
	
	private boolean hasCreateSampleCodeSet() {
		Set<ViewName> keySet = gadgetViewData.keySet();
		for (ViewName viewName : keySet) {
			GadgetViewData viewData = gadgetViewData.get(viewName);
			if (viewData.getType().equals(ViewType.html)) {
				if (viewData.isCreateSampleCodeSet()) {
					return true;
				}
			}
		}
		return false;
	}

}
