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
package com.googlecode.osde.internal.runtime.igoogle;

import java.net.MalformedURLException;
import java.net.URL;

import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The job to publish a gadget against iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 *
 */
public class PublishIGoogleJob extends BaseIGoogleJob {
    private static Logger logger = new Logger(PublishIGoogleJob.class);

    private Shell shell;
    private String projectName; // contains only chars of A-Z, a-z, or 0-9.

    public PublishIGoogleJob(Shell shell, String username, String password, String projectName,
            IFile gadgetXmlIFile) {
        super("iGoogle - Publish Gadget", username, password, gadgetXmlIFile);
        this.shell = shell;
        this.projectName = projectName;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running PublishIGoogleJob", 3);

        final String publishGadgetUrl;
        try {
            String urlOfHostedFile =
                    uploadFilesToIg(OSDE_PUBLISH_DIRECTORY + projectName + "/", false);
            publishGadgetUrl = HostingIGoogleUtil.formPublishGadgetUrl(urlOfHostedFile);

        } catch (HostingException e) {
            logger.warn(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);

        display.syncExec(new PublishingRunnable(publishGadgetUrl));
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
    }

    private static class PublishingRunnable implements Runnable {
        private static final String PUBLISH_IGOOGLE_BROWSER_ID = "publish_ig_bid";
        private static final String PUBLISH_IGOOGLE_BROWSER_NAME = "Publish gadget on iGoogle";
        private static final String PUBLISH_IGOOGLE_TOOLTIP = "Publish gadget on iGoogle";
        private String publishGadgetUrl;

        private PublishingRunnable(String publishGadgetUrl) {
            this.publishGadgetUrl = publishGadgetUrl;
        }

        public void run() {
            logger.fine("publishGadgetUrl: " + publishGadgetUrl);
            try {
                IWorkbenchBrowserSupport support =
                        PlatformUI.getWorkbench().getBrowserSupport();
                int style = IWorkbenchBrowserSupport.LOCATION_BAR
                          | IWorkbenchBrowserSupport.NAVIGATION_BAR
                          | IWorkbenchBrowserSupport.AS_EDITOR;
                IWebBrowser browser = support.createBrowser(style, PUBLISH_IGOOGLE_BROWSER_ID,
                        PUBLISH_IGOOGLE_BROWSER_NAME, PUBLISH_IGOOGLE_TOOLTIP);
                // TODO: (p1) Provide SID cookie when open the browser so that
                // the user does not need to login again on the publish page.
                // Or, we need 2 features of: host file and publish file.
                URL url = new URL(publishGadgetUrl);
                browser.openURL(url);
            } catch (MalformedURLException e) {
                logger.warn(e.getMessage());
            } catch (PartInitException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}
