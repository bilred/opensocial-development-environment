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
package com.googlecode.osde.internal.ui.wizards.newjsprj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Set;

import com.googlecode.osde.internal.gadgets.ViewName;
import com.googlecode.osde.internal.gadgets.ViewType;
import com.googlecode.osde.internal.utils.Gadgets;
import com.googlecode.osde.internal.utils.ResourceUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class GadgetXmlFileGenerator {

    private IProject project;

    private GadgetXmlData gadgetXmlData;

    private EnumMap<ViewName, GadgetViewData> gadgetViewData;

    public GadgetXmlFileGenerator(
            IProject project, GadgetXmlData gadgetXmlData,
            EnumMap<ViewName, GadgetViewData> gadgetViewData) {
        super();
        this.project = project;
        this.gadgetXmlData = gadgetXmlData;
        this.gadgetViewData = gadgetViewData;
    }

    public IFile generate(IProgressMonitor monitor) throws IOException, CoreException {
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
            if (gadgetXmlData.isOpensocial09()) {
                content += "    <Require feature=\"opensocial-0.9\" />\n";
            }
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
            if (gadgetXmlData.isOpensocial09() && hasCreateSampleCodeSet()) {
                content += "    <Require feature=\"osapi\" />\n";
            }
            content += "  </ModulePrefs>\n";
            Set<ViewName> keySet = gadgetViewData.keySet();
            for (ViewName viewName : keySet) {
                GadgetViewData viewData = gadgetViewData.get(viewName);
                if (viewData.getType().equals(ViewType.html)) {
                    content += "  <Content type=\"html\" view=\"" + viewName.toString()
                            + "\"><![CDATA[\n";
                    if (viewData.isCreateSampleCodeSet()) {
                        content += "\n";
                        if (viewData.isCreatePeople() && gadgetXmlData.isOpensocial08()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/fetch_people_08.txt");
                        }
                        if (viewData.isCreatePeople() && gadgetXmlData.isOpensocial09()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/fetch_people_09.txt");
                        }
                        if (viewData.isCreateActivity() && gadgetXmlData.isOpensocial08()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/post_activity_08.txt");
                        }
                        if (viewData.isCreateActivity() && gadgetXmlData.isOpensocial09()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/post_activity_09.txt");
                        }
                        if (viewData.isCreateAppData() && gadgetXmlData.isOpensocial08()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/share_appdata_08.txt");
                        }
                        if (viewData.isCreateAppData() && gadgetXmlData.isOpensocial09()) {
                            content += ResourceUtil
                                    .loadTextResourceFile("/samples/share_appdata_09.txt");
                        }
                    } else {
                        if (viewData.isCreateExternalJavaScript()) {
                            content += "\n";
                            content +=
                                    "<script type=\"text/javascript\" src=\"http://localhost:8080/"
                                            + project.getName() + "/" + viewData.getFilename()
                                            + "\"></script>\n";
                            if (viewData.isCreateInitFunction()) {
                                content += "\n";
                                content += "<script type=\"text/javascript\">\n";
                                content += "gadgets.util.registerOnLoadHandler(init);\n";
                                content += "</script>\n";
                            }
                        }
                        content += "\n";
                        content += "<!-- The code for " + viewName.getDisplayName()
                                + " view is here. -->\n";
                        content += "<div>" + viewName.getDisplayName() + " view for "
                                + gadgetXmlData.getTitle() + ".</div>\n";
                        content += "\n";
                    }
                    content += "  ]]></Content>\n";
                } else if (viewData.getType().equals(ViewType.url)) {
                    content +=
                            "  <Content type=\"url\" view=\"" + viewName.toString() + "\" href=\""
                                    + Gadgets.escapeAttributeValue(viewData.getHref()) + "\" />\n";
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
