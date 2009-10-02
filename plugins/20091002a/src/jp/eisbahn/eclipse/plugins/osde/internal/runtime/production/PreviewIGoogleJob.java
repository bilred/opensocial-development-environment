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

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrieveIgPrefEditToken;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrievePublicId;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrieveSid;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.uploadFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.IgPrefEditToken;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.core.resources.IFile;
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

/**
 * The job to open a browser and preview a gadget against iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 *
 */
public class PreviewIGoogleJob extends Job {
    private static Logger logger = Logger.getLogger(PreviewIGoogleJob.class.getName());
    private static final String HOST_DIR_FOR_OSDE = "osde/";

    private String jobName;
    private Shell shell;
    private String username;
    private String password;
    private boolean useExternalBrowser;
    private IFile gadgetXmlIFile;

    public PreviewIGoogleJob(String jobName, String username, String password,
            boolean useExternalBrowser, IFile gadgetXmlIFile) {
        super(jobName);
        this.jobName = jobName;
        this.username = username;
        this.password = password;
        this.useExternalBrowser = useExternalBrowser;
        this.gadgetXmlIFile = gadgetXmlIFile;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        final String previewGadgetUrl;
        try {
            previewGadgetUrl = uploadFileToIg();
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return Status.CANCEL_STATUS;
        }

        shell.getDisplay().syncExec(new Runnable() {
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
                        String browserId = null;
                        String browserName = jobName;
                        String tooltip = jobName;
                        browser = support.createBrowser(style, browserId, browserName, tooltip);
                    }
                    browser.openURL(new URL(previewGadgetUrl));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });
        return Status.OK_STATUS;
    }

    private String uploadFileToIg()
            throws ClientProtocolException, IOException, CoreException {
        // TODO: Support save SID etc in session.
        // TODO: Support captcha.
        logger.fine("in PreviewIGoogleJob.uploadFileToIg");
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);
        File sourceFile = gadgetXmlIFile.getRawLocation().toFile();
        String targetFilePath = HOST_DIR_FOR_OSDE + sourceFile.getName();
        String uploadFileResult =
            uploadFile(sid, publicId, prefEditToken, sourceFile, targetFilePath);
        logger.fine("uploadFileResult: " + uploadFileResult);
        String previewGadgetUrl = HostingIGoogleUtil.formPreviewGadgetUrl(publicId, targetFilePath);
        return previewGadgetUrl;
    }

    String uploadFilesToIg()
            throws ClientProtocolException, IOException, CoreException {
        // TODO: Support save SID etc in session.
        // TODO: Support captcha.
        logger.fine("in PreviewIGoogleJob.uploadFilesToIg");
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);

        File gadgetXmlFile = gadgetXmlIFile.getRawLocation().toFile();
        logger.info("gadgetXmlFile: " + gadgetXmlFile);
        File sourceDir = gadgetXmlFile.getParentFile();
        logger.info("sourceDir: " + sourceDir);

        String targetFilePath = HOST_DIR_FOR_OSDE + gadgetXmlFile.getName();
        String uploadFileResult =
            uploadFile(sid, publicId, prefEditToken, gadgetXmlFile, targetFilePath);
        logger.fine("uploadFileResult: " + uploadFileResult);
        String previewGadgetUrl = HostingIGoogleUtil.formPreviewGadgetUrl(publicId, targetFilePath);
        return previewGadgetUrl;
    }
}