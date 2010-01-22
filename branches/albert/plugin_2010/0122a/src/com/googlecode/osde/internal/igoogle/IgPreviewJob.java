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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The job to open a browser and preview a gadget against iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgPreviewJob extends Job {
    private static Logger logger = new Logger(IgPreviewJob.class);
    static final String OSDE_PREVIEW_DIRECTORY = "/osde/preview/";

    private String username;
    private String password;
    private IFile gadgetXmlIFile;
    private Shell shell;
    private boolean useCanvasView;
    private boolean useExternalBrowser;

    public IgPreviewJob(String username, String password, IFile gadgetXmlIFile,
            Shell shell, boolean useCanvasView, boolean useExternalBrowser) {
        super("iGoogle - Preview Gadget");
        this.username = username;
        this.password = password;
        this.gadgetXmlIFile = gadgetXmlIFile;
        this.shell = shell;
        this.useCanvasView = useCanvasView;
        this.useExternalBrowser = useExternalBrowser;
    }

    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running PreviewIGoogleJob", 3);

        final String previewGadgetUrl;
        try {
            IgCredentials igCredentials = new IgCredentials(username, password);

            // Get hosting URL.
            String hostingUrl = IgHostingUtil.formHostingUrl(
                    igCredentials.getPublicId(), OSDE_PREVIEW_DIRECTORY);

            // Modify gadget file with new hosting url.
            modifyHostingUrlForGadgetFileInTargetFolder(hostingUrl);

            // TODO: (p1) get rid of gadgetXmlIFile from parameters
            String urlOfHostedGadgetFile = IgHostingUtil.uploadFiles(
                    igCredentials, gadgetXmlIFile, OSDE_PREVIEW_DIRECTORY, useExternalBrowser);
            previewGadgetUrl = IgHostingUtil.formPreviewOpenSocialGadgetUrl(
                    urlOfHostedGadgetFile, useCanvasView, igCredentials.getSid());
        } catch (IgException e) {
            logger.warn(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);

        display.syncExec(new PreviewingRunnable(previewGadgetUrl, useExternalBrowser));
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
    }

    /**
     * Replaces "http://localhost:8080/" with newHostingUrl in the gadgetXmlIFile
     * which is copied into the target folder.
     */
    void modifyHostingUrlForGadgetFileInTargetFolder(String newHostingUrl) {
        // TODO: (p1) implement modifyHostingBaseUrlForGadgetFile
        IProject project = gadgetXmlIFile.getProject();
        String targetFolder =
            project.getFolder(GadgetBuilder.TARGET_FOLDER_NAME).getLocation().toOSString();
        String gadgetFileName = gadgetXmlIFile.getName();
        String gadgetFileFullPath = targetFolder + gadgetFileName;
        logger.info("gadgetFileFullPath: " + gadgetFileFullPath);
        try {
            FileReader fileReader = new FileReader(gadgetFileFullPath);
            String fileContentAsString = IOUtils.toString(fileReader);
            logger.info("fileContentAsString: " + fileContentAsString);
            String modifiedFileContent =
                fileContentAsString.replaceAll("http://localhost:8080/", newHostingUrl);
            logger.info("modifiedFileContent: " + modifiedFileContent);

            // TODO: (p0) Write file to target folder
            FileWriter modifiedGadgetXmlFile = new FileWriter("test/com/googlecode/osde/internal/igoogle/testdata/osde_preview.xml"); // TODO: (p0) make this a constant
            //IOUtils.write(modifiedFileContent, modifiedGadgetXmlFile);
            modifiedGadgetXmlFile.write(modifiedFileContent);
            modifiedGadgetXmlFile.flush();
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        } catch (IOException e) {
            logger.warn(e.getMessage());
        } finally {
            // TODO: (p0) close stream/reader
        }

    }

    private static class PreviewingRunnable implements Runnable {
        private static final String PREVIEW_IGOOGLE_BROWSER_ID = "preview_ig_bid";
        private static final String PREVIEW_IGOOGLE_BROWSER_NAME = "Preview gadget on iGoogle";
        private static final String PREVIEW_IGOOGLE_TOOLTIP = "Preview gadget on iGoogle";

        private String previewGadgetUrl;
        private boolean useExternalBrowser;

        private PreviewingRunnable(String previewGadgetUrl, boolean useExternalBrowser) {
            this.previewGadgetUrl = previewGadgetUrl;
            this.useExternalBrowser = useExternalBrowser;
        }

        public void run() {
            logger.fine("in Runnable's run");
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
                    browser = support.createBrowser(style, PREVIEW_IGOOGLE_BROWSER_ID,
                            PREVIEW_IGOOGLE_BROWSER_NAME, PREVIEW_IGOOGLE_TOOLTIP);
                }
                browser.openURL(new URL(previewGadgetUrl));
            } catch (MalformedURLException e) {
                logger.warn(e.getMessage());
            } catch (PartInitException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}