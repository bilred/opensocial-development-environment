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
package com.googlecode.osde.internal.ui.views.apps;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.shindig.ShindigLauncher;
import com.googlecode.osde.internal.ui.views.AbstractView;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;

public class ApplicationView extends AbstractView {

    public static final String ID = "com.googlecode.osde.internal.views.ApplicationView";

    private Action reloadAction;
    private Action removeAllAction;

    private ApplicationsBlock block;

    public ApplicationView() {
    }

    @Override
    protected void fillContextMenu(IMenuManager manager) {
        super.fillContextMenu(manager);
        manager.add(reloadAction);
        manager.add(removeAllAction);
    }

    @Override
    protected void fillLocalPullDown(IMenuManager manager) {
        super.fillLocalPullDown(manager);
        manager.add(reloadAction);
        manager.add(removeAllAction);
    }

    @Override
    protected void fillLocalToolBar(IToolBarManager manager) {
        super.fillLocalToolBar(manager);
        manager.add(reloadAction);
        manager.add(removeAllAction);
    }

    @Override
    protected void makeActions() {
        super.makeActions();
        reloadAction = new Action() {
            @Override
            public void run() {
                loadApplications();
            }
        };
        reloadAction.setText("Reload");
        reloadAction.setToolTipText("Reload applications.");
        reloadAction.setImageDescriptor(
                Activator.getDefault().getImageRegistry().getDescriptor(
                        "icons/action_refresh.gif"));
        removeAllAction = new Action() {
            @Override
            public void run() {
                removeAllApplications();
            }
        };
        removeAllAction.setText("Remove all");
        removeAllAction.setToolTipText("Remove all applications.");
        removeAllAction.setImageDescriptor(
                Activator.getDefault().getImageRegistry().getDescriptor("icons/16-em-cross.gif"));
    }

    protected void createForm(Composite parent) {
        IManagedForm managedForm = new ManagedForm(parent);
        block = new ApplicationsBlock(this);
        block.createContent(managedForm);
    }

    public void setFocus() {
    }

    public void loadApplications() {
        if (Activator.getDefault().isRunningShindig()) {
            try {
                ApplicationService applicationService = Activator.getDefault().getApplicationService();
                List<ApplicationImpl> applications = applicationService.getApplications();
                block.setApplications(applications);
            } catch (ConnectionException e) {
                MessageDialog.openError(getSite().getShell(), "Error",
                        "Shindig database not started yet.");
            }
        } else {
            ShindigLauncher.launchWithConfirm(getSite().getShell(), this);
        }
    }

    public void connectedDatabase() {
        loadApplications();
    }

    public void disconnectedDatabase() {
        block.setApplications(new ArrayList<ApplicationImpl>());
    }

    private void removeAllApplications() {
        try {
            ApplicationService applicationService = Activator.getDefault().getApplicationService();
            if (MessageDialog.openConfirm(getSite().getShell(), "Confirm",
                    "Would you like to delete all applications?")) {
                applicationService.removeAll();
                loadApplications();
            }
        } catch (ConnectionException e) {
            MessageDialog
                    .openError(getSite().getShell(), "Error", "Shindig database not started yet.");
        }
    }

}