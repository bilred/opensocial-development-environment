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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.googlecode.osde.internal.Activator;
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
    static final String LOCAL_HOST_URL = "http://localhost:8080/";
    static final String GADGET_FILE_WITH_MODIFIED_URL = "modified_gadget.xml";

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

            // Modify gadget file with new hosting url, and upload it.
            String eclipseProjectName = gadgetXmlIFile.getProject().getName();
            String oldHostingUrl = LOCAL_HOST_URL + eclipseProjectName + "/";
            modifyHostingUrlForGadgetFileAndUploadIt(oldHostingUrl, hostingUrl, igCredentials,
                    OSDE_PREVIEW_DIRECTORY);

            // Upload files.
            IgHostingUtil.uploadFiles(igCredentials, gadgetXmlIFile.getProject(),
                    OSDE_PREVIEW_DIRECTORY, useExternalBrowser);

            // Form hosted gadget file url.
            String urlOfHostedGadgetFile = hostingUrl + GADGET_FILE_WITH_MODIFIED_URL;

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
     * Replaces oldHostingUrl with newHostingUrl in the gadgetXmlIFile
     * which is copied into the target folder.
     * Then upload it to iGoogle.
     *
     * @throws IgException
     */
    void modifyHostingUrlForGadgetFileAndUploadIt(String oldHostingUrl, String newHostingUrl,
            IgCredentials igCredentials, String hostingFolder)
            throws IgException {
        // Get gadget file full path.
        IProject project = gadgetXmlIFile.getProject();
        String targetFolder =
            project.getFolder(GadgetBuilder.TARGET_FOLDER_NAME).getLocation().toOSString();
        String gadgetFileName = gadgetXmlIFile.getName();
        String gadgetFileFullPath = targetFolder + "/" + gadgetFileName;
        logger.info("gadgetFileFullPath: " + gadgetFileFullPath);

        // Read content from gadget file, and then modify them and save as a new gadget file.
        FileReader fileReader = null;
        FileWriter modifiedGadgetXmlFile = null;
        try {
            fileReader = new FileReader(gadgetFileFullPath);
            String fileContentAsString = IOUtils.toString(fileReader);
            logger.fine("fileContentAsString:\n" + fileContentAsString);
            String modifiedFileContent =
                fileContentAsString.replaceAll(oldHostingUrl, newHostingUrl);
            logger.fine("modifiedFileContent:\n" + modifiedFileContent);

            // Prepare the modified gadget file.
            File osdeWorkFolder = getOsdeWorkFolder();
            File modifiedFile = new File(osdeWorkFolder, GADGET_FILE_WITH_MODIFIED_URL);
            if (modifiedFile.exists()) {
                modifiedFile.delete();
            }
            boolean isCreated = modifiedFile.createNewFile();
            logger.info("isCreated: " + isCreated);

            // Write modified content to the new modified gadget file.
            modifiedGadgetXmlFile = new FileWriter(modifiedFile);
            modifiedGadgetXmlFile.write(modifiedFileContent);
            modifiedGadgetXmlFile.flush();

            // Upload the modified gadget file to iGoogle.
            IgHostingUtil.uploadFile(igCredentials, osdeWorkFolder.getAbsolutePath(),
                    GADGET_FILE_WITH_MODIFIED_URL, hostingFolder);

        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new IgException(e);
        } finally {
            IOUtils.closeQuietly(fileReader);
            IOUtils.closeQuietly(modifiedGadgetXmlFile);
        }
    }

    static File getOsdeWorkFolder() {
        String userHome = System.getProperty("user.home");
        File osdeWorkFolder = new File(userHome, Activator.WORK_DIR_NAME);
        if (!osdeWorkFolder.exists()) {
            osdeWorkFolder.mkdir();
        }
        return osdeWorkFolder;
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