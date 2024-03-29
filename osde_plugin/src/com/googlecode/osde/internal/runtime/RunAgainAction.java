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
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.runtime;

import com.googlecode.osde.internal.Activator;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action to be run again locally.
 */
public class RunAgainAction extends AbstractRunAction implements IWorkbenchWindowActionDelegate {

    public void dispose() {
    }

    public void run(IAction action) {
        LaunchApplicationInformation information =
                Activator.getDefault().getLastApplicationInformation();
        if (information == null) {
            MessageDialog.openWarning(shell, "Warning", "Any application is not started yet.");
        } else {
            Job job = new LaunchApplicationJob("Running application", information, shell);
            job.schedule();
            notifyUserPrefsView(information);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

}
