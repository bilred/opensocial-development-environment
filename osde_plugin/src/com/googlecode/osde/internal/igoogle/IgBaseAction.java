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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * The base Action for iGoogle-related features.
 *
 * @author albert.cheng.ig@gmail.com
 */
public abstract class IgBaseAction
        implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {
    private static Logger logger = new Logger(IgBaseAction.class);

    private Shell shell;
    Shell getShell() {
        return shell;
    }

    public void init(IWorkbenchWindow window) {
        IWorkbenchPart targetPart = window.getActivePage().getActivePart();
        shell = targetPart.getSite().getShell();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // Do nothing by default.
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        logger.fine("in setActivePart");
        shell = targetPart.getSite().getShell();
    }

    public void dispose() {
        // Do nothing by default.
    }

    IgCredentials retrieveCredentials(IgCredentialsDialog dialog) {
        IgCredentials igCredentials = null;
        if (dialog.isUsePreviousIgCredentials()) {
            igCredentials = IgCredentials.getCurrentInstance();
        } else {
            String username = dialog.getUsername();
            String password = dialog.getPassword();
            try {
                igCredentials = IgCredentials.createCurrentInstance(username, password);
            } catch (IgException e) {
                logger.error("Invalid iGoogle Credentials (username, password)");
            }
        }
        return igCredentials;
    }
}
