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
package com.googlecode.osde.internal.runtime.production;

import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * The Action for cleaning preview files as hosted on iGoogle server.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class CleanIGoogleAction
        implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {
    private static Logger logger = Logger.getLogger(CleanIGoogleAction.class.getName());

    private Shell shell;

    public void init(IWorkbenchWindow window) {
        logger.fine("in init");
        IWorkbenchPart targetPart = window.getActivePage().getActivePart();
        shell = targetPart.getSite().getShell();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        logger.fine("in selectionChanged");
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        logger.fine("in setActivePart");
        shell = targetPart.getSite().getShell();
    }

    public void run(IAction action) {
        logger.fine("in run");
        CleanIGoogleDialog dialog = new CleanIGoogleDialog(shell);
        int openResult = dialog.open();
        if (openResult == Window.OK) {
            String username = dialog.getUsername();
            String password = dialog.getPassword();
            Job job = new CleanIGoogleJob(shell, username, password);
            job.schedule();
        }
    }

    public void dispose() {
        logger.fine("in dispose");
    }
}
