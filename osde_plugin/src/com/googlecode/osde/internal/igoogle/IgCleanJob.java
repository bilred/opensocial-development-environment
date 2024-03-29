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

import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The job to clean files as hosted on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgCleanJob extends Job {
    private static Logger logger = new Logger(IgCleanJob.class);

    private Shell shell;
    IgCredentials igCredentials;
    private String hostProjectName;

    public IgCleanJob(Shell shell, IgCredentials igCredentials, String hostProjectName) {
        super("iGoogle - Clean Host Files");
        this.shell = shell;
        this.igCredentials = igCredentials;
        this.hostProjectName = hostProjectName;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running CleanIGoogleJob", 3);
        monitor.worked(1);

        // Get hosting folder.
        String hostingFolder = IgHostFileJob.OSDE_HOST_BASE_FOLDER + hostProjectName + "/";
        try {
            // Clean all files as hosted on iGoogle.
            IgHostingUtil.cleanFiles(igCredentials, hostingFolder);
        } catch (IgException e) {
            logger.warn(e.getMessage());
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
            String dialogTitle = "Success!";
            String dialogMessage = "All your files hosted on iGoogle for \"" + hostProjectName
                    + "\" have been deleted.";
            int dialogImageType = MessageDialog.INFORMATION;
            Image dialogTitleImage = null;
            String[] dialogButtonLabels = {"OK"};
            int defaultIndex = 0;
            MessageDialog dialog = new MessageDialog(shell, dialogTitle, dialogTitleImage,
                    dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
            int openResult = dialog.open();
            logger.fine("openResult: " + openResult);
        }
    }
}
