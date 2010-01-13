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
package com.googlecode.osde.internal.igoogle;

import java.util.List;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The job to host gadget files on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgHostJob extends Job {
    private static Logger logger = new Logger(IgHostJob.class);

    private String username;
    private String password;
    private String projectName; // contains only chars of A-Z, a-z, or 0-9.
    private Shell shell;

    // TOD: (p1) Actually, gadgetXmlIFile is not necessary for hosting files.
    // Use a project instead of an IFile
    private IFile gadgetXmlIFile;

    public IgHostJob(String username, String password, String projectName, Shell shell,
            IFile gadgetXmlIFile) {
        super("iGoogle - Host Gadget");
        this.username = username;
        this.password = password;
        this.projectName = projectName;
        this.shell = shell;
        this.gadgetXmlIFile = gadgetXmlIFile;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running PreviewIGoogleJob", 3);

        try {
            // TODO: (p0) refactor uploadFilesToIg's return
            List<String> urlsOfHostedGadgetFile = uploadFilesToIg(projectName);
        } catch (IgException e) {
            logger.warn(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);

        display.syncExec(new HostingRunnable());
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
    }

    // TODO: (p0) return list of URLs of hosted files.
    /**
     * Uploads files to iGoogle and returns the url's for the hosted gadget files.
     *
     * @throws IgException
     */
    protected List<String> uploadFilesToIg(String hostingFolder)
            throws IgException {
        logger.fine("in PreviewIGoogleJob.uploadFilesToIg");

        // Prepare params.
        // TODO: (p2) Support save SID etc in session.
        // TODO: (p2) Support captcha.
        IgCredentials igCredentials = new IgCredentials(username, password);
        IProject project = gadgetXmlIFile.getProject();
        String uploadFromPath =
                project.getFolder(GadgetBuilder.TARGET_FOLDER_NAME).getLocation().toOSString();

        // Upload files.
        IgHostingUtil.uploadFiles(igCredentials, uploadFromPath, hostingFolder);

        return null;
    }

    private static class HostingRunnable implements Runnable {
        public void run() {
            // TODO: (p0) display a message dialog showing list of url's.
            logger.fine("in Runnable's run");
//            try {
//                IWorkbenchBrowserSupport support =
//                        PlatformUI.getWorkbench().getBrowserSupport();
//                IWebBrowser browser;
//                if (useExternalBrowser) {
//                    browser = support.getExternalBrowser();
//                } else {
//                    int style = IWorkbenchBrowserSupport.LOCATION_BAR
//                            | IWorkbenchBrowserSupport.NAVIGATION_BAR
//                            | IWorkbenchBrowserSupport.AS_EDITOR;
//                    browser = support.createBrowser(style, PREVIEW_IGOOGLE_BROWSER_ID,
//                            PREVIEW_IGOOGLE_BROWSER_NAME, PREVIEW_IGOOGLE_TOOLTIP);
//                }
//                browser.openURL(new URL(previewGadgetUrl));
//            } catch (MalformedURLException e) {
//                logger.warn(e.getMessage());
//            } catch (PartInitException e) {
//                logger.warn(e.getMessage());
//            }
        }
    }
}