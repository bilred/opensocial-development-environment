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

import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The job to clean preview files as hosted on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 *
 */
public class CleanIGoogleJob extends BaseIGoogleJob {
    private static Logger logger = Logger.getLogger(CleanIGoogleJob.class.getName());

    private Shell shell;

    public CleanIGoogleJob(Shell shell, String username, String password) {
        super("iGoogle - Clean Host Files", username, password);
        this.shell = shell;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running CleanIGoogleJob", 4);
        monitor.worked(1);

        // Clean all preview files as hosted on iGoogle.
        try {
            cleanFiles(OSDE_PREVIEW_DIRECTORY);
        } catch (HostingException e) {
            logger.warning(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);
        monitor.done();

        display.asyncExec(new CleaningRunnable());
        return Status.OK_STATUS;
    }

    private class CleaningRunnable implements Runnable {
        public void run() {
            String dialogTitle = "Your Files Are Cleaned.";
            String dialogMessage = "All your files as hosted at iGoogle for preview are cleaned.";
            int dialogImageType = MessageDialog.INFORMATION;
            Image dialogTitleImage = null;
            String[] dialogButtonLabels = {"OK"};
            int defaultIndex = 0;
            MessageDialog dialog = new MessageDialog(shell, dialogTitle, dialogTitleImage,
                    dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
            int openResult = dialog.open();
            logger.info("openResult: " + openResult);
        }
    }
}
