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
package com.googlecode.osde.internal.runtime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringTokenizer;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.profiling.ProfilingBrowserSupport;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.shindig.social.opensocial.hibernate.entities.UserPrefImpl;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.json.simple.JSONObject;

/**
 * Job for launching application with local shindig/database support.
 */
public class LaunchApplicationJob extends Job {

    private static final Logger logger = new Logger(LaunchApplicationJob.class);

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
	private String url;
	private String appTitle;
	private final IWorkbenchBrowserSupport browserSupport;

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
		this.url = information.getUrl();
		this.appTitle = information.getApplicationTitle();
		this.browserSupport = information.isMeasurePerformance() ?
				new ProfilingBrowserSupport(shell) : PlatformUI.getWorkbench().getBrowserSupport();
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Running application", 3);
			String upJson = createJsonFromUserPrefs();
			monitor.worked(1);
			final String kicker;
			if (project != null) {
				kicker = "http://localhost:8080/gadgets/files/osdecontainer/index.html?url=http://localhost:8080/" + project.getName().replace(" ", "%20") + "/"
						+ url.replace(" ", "%20")
						+ "&view=" + view
						+ "&viewerId=" + URLEncoder.encode(viewer, "UTF-8")
						+ "&ownerId=" + URLEncoder.encode(owner, "UTF-8")
						+ "&width=" + URLEncoder.encode(width, "UTF-8")
						+ "&appId=" + URLEncoder.encode(appId, "UTF-8")
						+ "&country=" + URLEncoder.encode(country, "UTF-8")
						+ "&language=" + URLEncoder.encode(language, "UTF-8")
						+ "&userPrefs=" + URLEncoder.encode(upJson, "UTF-8");
			} else {
				kicker = "http://localhost:8080/gadgets/files/osdecontainer/index.html?url="
				+ url.replace(" ", "%20")
				+ "&view=" + view
				+ "&viewerId=" + URLEncoder.encode(viewer, "UTF-8")
				+ "&ownerId=" + URLEncoder.encode(owner, "UTF-8")
				+ "&width=" + URLEncoder.encode(width, "UTF-8")
				+ "&appId=" + URLEncoder.encode(appId, "UTF-8")
				+ "&country=" + URLEncoder.encode(country, "UTF-8")
				+ "&language=" + URLEncoder.encode(language, "UTF-8")
				+ "&userPrefs=" + URLEncoder.encode(upJson, "UTF-8");
			}
			monitor.worked(1);
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						IWebBrowser browser;
						if (!useExternalBrowser) {
							String title = appTitle + " [" + view + "]";
							String desc = appTitle + " [" + view + "] viewer=" + viewer + " owner=" + owner;
							browser = browserSupport.createBrowser(
									IWorkbenchBrowserSupport.LOCATION_BAR
										| IWorkbenchBrowserSupport.NAVIGATION_BAR
										| IWorkbenchBrowserSupport.AS_EDITOR,
									title, title, desc);
						} else {
							browser = browserSupport.getExternalBrowser();
						}
						browser.openURL(new URL(kicker));
					} catch (MalformedURLException e) {
						logger.error("Launching the Web Browser failed.", e);
					} catch (PartInitException e) {
						logger.error("Launching the Web Browser failed.", e);
					}
				}
			});
			monitor.worked(1);
			monitor.done();
			return Status.OK_STATUS;
		} catch (UnsupportedEncodingException e) {
			logger.warn("Launching your application failed.", e);
			monitor.done();
			return Status.CANCEL_STATUS;
		} catch (ConnectionException e) {
			logger.warn("Launching your application failed.", e);
			monitor.done();
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
			return Status.CANCEL_STATUS;
		}
	}

	protected String createJsonFromUserPrefs() throws ConnectionException {
		ApplicationService service = Activator.getDefault().getApplicationService();
		List<UserPrefImpl> userPrefs = service.getUserPrefs(appId, viewer);
		JSONObject up = new JSONObject();
		for (UserPrefImpl userPref : userPrefs) {
			String value = userPref.getValue();
			StringTokenizer st = new StringTokenizer(value);
			String values = "";
			boolean first = true;
			while (st.hasMoreTokens()) {
				String v = st.nextToken();
				if (!first) {
					values += "|";
				}
				values += v;
				first = false;
			}
			up.put(userPref.getName(), values);
		}
		String upJson = up.toString();
		return upJson;
	}
}