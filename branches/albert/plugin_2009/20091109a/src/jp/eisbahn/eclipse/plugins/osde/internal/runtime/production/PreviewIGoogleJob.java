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
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil;

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
 * The job to open a browser and preview a gadget against iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 *
 */
public class PreviewIGoogleJob extends BaseIGoogleJob {
    private static Logger logger = Logger.getLogger(PreviewIGoogleJob.class.getName());

    static final String OSDE_PREVIEW_DIRECTORY = "/osde/preview/";
    private static final String PREVIEW_IGOOGLE_BROWSER_ID = "preview_ig_bid";
    private static final String PREVIEW_IGOOGLE_BROWSER_NAME = "Preview gadget on iGoogle";
    private static final String PREVIEW_IGOOGLE_TOOLTIP = "Preview gadget on iGoogle";

    private Shell shell;
    private boolean useCanvasView;
    private boolean useExternalBrowser;

    public PreviewIGoogleJob(String username, String password, IFile gadgetXmlIFile,
            Shell shell, boolean useCanvasView, boolean useExternalBrowser) {
        super(username, password, gadgetXmlIFile);
        this.shell = shell;
        this.useExternalBrowser = useExternalBrowser;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running PreviewIGoogleJob", 3);

        final String previewGadgetUrl;
        try {
            String urlOfHostedGadgetFile =
                    uploadFilesToIg(OSDE_PREVIEW_DIRECTORY, useExternalBrowser);
            previewGadgetUrl = HostingIGoogleUtil.formPreviewGadgetUrl(
                    urlOfHostedGadgetFile, useCanvasView);
        } catch (HostingException e) {
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
                        browser = support.createBrowser(style, PREVIEW_IGOOGLE_BROWSER_ID,
                                PREVIEW_IGOOGLE_BROWSER_NAME, PREVIEW_IGOOGLE_TOOLTIP);
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
}