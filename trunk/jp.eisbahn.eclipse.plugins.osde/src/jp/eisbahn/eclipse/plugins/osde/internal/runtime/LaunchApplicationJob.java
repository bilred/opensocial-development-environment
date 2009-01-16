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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class LaunchApplicationJob extends Job {

	private IProject project;
	private String viewer;
	private String owner;
	private String view;
	private String width;
	private String appId;
	private boolean useExternalBrowser;
	private String country;
	private String language;
	private Shell shell;
	private String gadgetXmlFileName;

	public LaunchApplicationJob(
			String name, LaunchApplicationInformation information, Shell shell) {
		super(name);
		this.project = information.getProject();
		this.viewer = information.getViewer();
		this.owner = information.getOwner();
		this.view = information.getView();
		this.width = information.getWidth();
		this.appId = information.getAppId();
		this.useExternalBrowser = information.isUseExternalBrwoser();
		this.country = information.getCountry();
		this.language = information.getLanguage();
		this.shell = shell;
		this.gadgetXmlFileName = information.getGadgetXmlFileName();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Running application", 1);
			int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
			final String url = "http://localhost:8080/gadgets/files/osdecontainer/index.html?url=http://localhost:" + port + "/"
					+ gadgetXmlFileName
					+ "&view=" + view
					+ "&viewerId=" + URLEncoder.encode(viewer, "UTF-8")
					+ "&ownerId=" + URLEncoder.encode(owner, "UTF-8")
					+ "&width=" + URLEncoder.encode(width, "UTF-8")
					+ "&appId=" + URLEncoder.encode(appId, "UTF-8")
					+ "&country=" + URLEncoder.encode(country, "UTF-8")
					+ "&language=" + URLEncoder.encode(language, "UTF-8");
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
						IWebBrowser browser;
						if (!useExternalBrowser) {
							String title = project.getName() + ":" + gadgetXmlFileName + " [" + view + "]";
							String desc = project.getName() + ":" + gadgetXmlFileName + " [" + view + "] viewer=" + viewer + " owner=" + owner;
							browser = support.createBrowser(
									IWorkbenchBrowserSupport.LOCATION_BAR 
										| IWorkbenchBrowserSupport.NAVIGATION_BAR
										| IWorkbenchBrowserSupport.AS_EDITOR,
									url, title, desc);
						} else {
							browser = support.getExternalBrowser();
						}
						browser.openURL(new URL(url));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					monitor.done();
				}
			});
			return Status.OK_STATUS;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			monitor.done();
			return Status.CANCEL_STATUS;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			monitor.done();
			return Status.CANCEL_STATUS;
		}
	}
}