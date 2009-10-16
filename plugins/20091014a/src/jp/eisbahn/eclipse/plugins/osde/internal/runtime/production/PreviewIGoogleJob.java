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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.IgPrefEditToken;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

    /**
     * This constant stands for the folder name of "target".
     * This folder stores all the gadget-related files.
     * This folder is created in
     * {@link jp.eisbahn.eclipse.plugins.osde.internal.builders.GadgetBuilder GadgetBuilder}
     * which determines what files are for deployment purpose.
     */
    // TODO: Reduce the impact caused by changes in GadgetBuilder:
    // The first approach is to make this a global constant and make
    // sure every corresponding code calls this constant instead of
    // using a string literal "target".
    // By doing this, any change in GadgetBuilder can be reflected to
    // all the places immediately, and thus reduce the impact from
    // changes in one place.
    // Also, we need to make sure this folder exists prior to using
    // it here.
    static final String TARGET_FOLDER_NAME = "target";

    private String jobName;
    private Shell shell;
    private String username;
    private String password;
    private boolean useExternalBrowser;
    private IFile gadgetXmlIFile;

    public PreviewIGoogleJob(String jobName, Shell shell, String username, String password,
            boolean useExternalBrowser, IFile gadgetXmlIFile) {
        super(jobName);
        this.jobName = jobName;
        this.shell = shell;
        this.username = username;
        this.password = password;
        this.useExternalBrowser = useExternalBrowser;
        this.gadgetXmlIFile = gadgetXmlIFile;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running PreviewIGoogleJob", 3);

        final String previewGadgetUrl;
        try {
            previewGadgetUrl = uploadFilesToIg();
        } catch (Exception e) {
            logger.warning(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);

        display.syncExec(new Runnable() {
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
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
    }

    /**
     * Uploads files to iGoogle.
     *
     * @return the url for gadget preview
     * @throws ClientProtocolException
     * @throws IOException
     * @throws CoreException
     * @throws HostingException
     */
    String uploadFilesToIg()
            throws ClientProtocolException, IOException, CoreException, HostingException {
        // TODO: Support save SID etc in session.
        // TODO: Support captcha.
        logger.fine("in PreviewIGoogleJob.uploadFilesToIg");
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);

        IProject project = gadgetXmlIFile.getProject();
        String targetPath = project.getFolder(TARGET_FOLDER_NAME).getLocation().toOSString();
        logger.fine("targetPath: " + targetPath);

        // Upload files.
        uploadFiles(sid, publicId, prefEditToken, targetPath);

        // Form url for preview gadget.
        String previewGadgetUrl = formPreviewGadgetUrl(publicId, gadgetXmlIFile.getName());
        return previewGadgetUrl;
    }
}