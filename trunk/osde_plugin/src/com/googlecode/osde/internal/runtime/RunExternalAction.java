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

import java.io.IOException;
import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.shindig.PersonService;
import com.googlecode.osde.internal.ui.views.userprefs.UserPrefsView;
import com.googlecode.osde.internal.utils.ApplicationInformation;
import com.googlecode.osde.internal.utils.Logger;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import com.google.gadgets.parser.ParserException;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

/**
 * Action to run external action.
 */
public class RunExternalAction implements IWorkbenchWindowActionDelegate {
    private static final Logger logger = new Logger(RunExternalAction.class);
    private Shell shell;
    private IWorkbenchPart targetPart;

    public RunExternalAction() {
        super();
    }

    public void run(IAction action) {
        try {
            PersonService personService = Activator.getDefault().getPersonService();
            List<Person> people = personService.getPeople();
            RunExternalApplicationDialog dialog = new RunExternalApplicationDialog(shell, people);
            if (dialog.open() == Window.OK) {
                String url = dialog.getUrl();
                ApplicationInformation appInfo = OpenSocialUtil.createApplicationInformation(url);
                ApplicationService applicationService =
                        Activator.getDefault().getApplicationService();
                applicationService.storeAppInfo(appInfo);
                String view = dialog.getView();
                String viewer = dialog.getViewer();
                String owner = dialog.getOwner();
                String width = dialog.getWidth();
                String appId = appInfo.getAppId();
                boolean useExternalBrowser = dialog.isUseExternalBrowser();
                String country = dialog.getCountry();
                String language = dialog.getLanguage();
                boolean measurePerformance = dialog.isMeasurePerformance();
                LaunchApplicationInformation information = new LaunchApplicationInformation(
                        viewer, owner, view, width, appId, useExternalBrowser,
                        country, language, null, url,
                        appInfo.getModule().getModulePrefs().getTitle(), measurePerformance);
                Job job = new LaunchApplicationJob("Running application", information, shell);
                job.schedule();
                notifyUserPrefsView(information);
            }
        } catch (ConnectionException e) {
            MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
        } catch (CoreException e) {
            logger.warn("Invalid gadget file.", e);
            MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Invalid gadget file.", e);
            MessageDialog.openError(shell, "Error", "Invalid gadget file.\n" + e.getMessage());
        } catch (ParserException e) {
            logger.warn("Invalid gadget file.", e);
            MessageDialog.openError(shell, "Error", "Invalid gadget file.\n" + e.getMessage());
        }
    }

    private void notifyUserPrefsView(final LaunchApplicationInformation information) {
        final IWorkbenchWindow window = targetPart.getSite().getWorkbenchWindow();
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                try {
                    UserPrefsView userPrefsView;
                    userPrefsView =
                            (UserPrefsView) window.getActivePage().showView(UserPrefsView.ID);
                    userPrefsView.showUserPrefFields(information, information.getUrl());
                } catch (PartInitException e) {
                    logger.warn("Notifying to UserPrefs view failed.", e);
                }
            }
        });
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        targetPart = window.getActivePage().getActivePart();
        shell = targetPart.getSite().getShell();
    }

}
