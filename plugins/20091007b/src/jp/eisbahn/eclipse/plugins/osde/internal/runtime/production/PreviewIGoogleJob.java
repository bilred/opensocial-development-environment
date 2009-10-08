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

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.IgPrefEditToken;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.core.runtime.CoreException;
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
 *
 */
public class PreviewIGoogleJob extends Job {
    private static Logger logger = Logger.getLogger(PreviewIGoogleJob.class.getName());

    private String jobName;
    private Shell shell;
    private String username;
    private String password;
    private boolean useExternalBrowser;
    private File gadgetXmlFile;

    public PreviewIGoogleJob(String jobName, Shell shell, String username, String password,
            boolean useExternalBrowser, File gadgetXmlFile) {
        super(jobName);
        this.jobName = jobName;
        this.shell = shell;
        this.username = username;
        this.password = password;
        this.useExternalBrowser = useExternalBrowser;
        this.gadgetXmlFile = gadgetXmlFile;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.info("in run");
        final String previewGadgetUrl;
        try {
            logger.info("gonna uploadFilesToIg()");
            previewGadgetUrl = uploadFilesToIg();
            logger.info("finished uploadFilesToIg()");
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return Status.CANCEL_STATUS;
        }

        logger.info("gonna syncExec");
        logger.info("shell: " + shell);
        Display display = shell.getDisplay();
        logger.info("display: " + display);
        display.syncExec(new Runnable() {
            public void run() {
                logger.info("in Runnable's run");
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
        logger.info("finished syncExec");
        return Status.OK_STATUS;
    }

//    private String uploadFileToIg()
//            throws ClientProtocolException, IOException, CoreException, HostingException {
//        // TODO: Support save SID etc in session.
//        // TODO: Support captcha.
//        logger.fine("in PreviewIGoogleJob.uploadFileToIg");
//        String sid = retrieveSid(username, password, null, null);
//        String publicId = retrievePublicId(sid);
//        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);
//        uploadFile(sid, publicId, prefEditToken, gadgetXmlFile);
//        String targetFilePath = gadgetXmlFile.getName();
//        String previewGadgetUrl = HostingIGoogleUtil.formPreviewGadgetUrl(publicId, targetFilePath);
//        return previewGadgetUrl;
//    }

    String uploadFilesToIg()
            throws ClientProtocolException, IOException, CoreException, HostingException {
        // TODO: Support save SID etc in session.
        // TODO: Support captcha.
        logger.info("in PreviewIGoogleJob.uploadFilesToIg");
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);
        logger.info("prefEditToken: " + prefEditToken);

        // TODO: get list of files in target folder
        ArrayList <File> listOfFiles = new ArrayList <File> ();
        listOfFiles.add(gadgetXmlFile);

        logger.info("gadgetXmlFile: " + gadgetXmlFile);
        File sourceDir = gadgetXmlFile.getParentFile();
        logger.info("sourceDir: " + sourceDir);

        ArrayList <String> relativeFilePaths = new ArrayList <String> ();
        relativeFilePaths.add(gadgetXmlFile.getName());
        String rootPath = gadgetXmlFile.getParent();
        uploadFiles(sid, publicId, prefEditToken, rootPath, relativeFilePaths);

//        uploadFiles(sid, publicId, prefEditToken, listOfFiles);
        String previewGadgetUrl =
                HostingIGoogleUtil.formPreviewGadgetUrl(publicId, gadgetXmlFile.getName());
        return previewGadgetUrl;
    }
}