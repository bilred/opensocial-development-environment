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
 * specific language governing permissions and limitations under
 * the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.runtime.production;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The job to open a browser and preview a gadget against iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 *
 */
public class PreviewIGoogleJob extends Job {

    private String username;
    private String password;
	private boolean useExternalBrowser;
	private Shell shell;
	private String jobName;

	public PreviewIGoogleJob(String jobName, Shell shell, String username, String password,
	        boolean useExternalBrowser, IFile gadgetXmlFile) {
		super(jobName);
		this.jobName = jobName;
        this.shell = shell;
        this.username = username;
        this.password = password;
        this.useExternalBrowser = useExternalBrowser;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
	    // TODO implement upload file.

		final String url = "http://www.google.com/ig";

		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchBrowserSupport support =
					        PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser;
					if (useExternalBrowser) {
                        browser = support.getExternalBrowser();
					} else {
					    int style = IWorkbenchBrowserSupport.LOCATION_BAR
                                  | IWorkbenchBrowserSupport.NAVIGATION_BAR
                                  | IWorkbenchBrowserSupport.AS_EDITOR;
					    String browserId = null;
					    String browserName = jobName;
					    String tooltip = jobName;
						browser = support.createBrowser(style, browserId, browserName, tooltip);
					}
					browser.openURL(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
		return Status.OK_STATUS;
	}
}