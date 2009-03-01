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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.util.List;

import javax.xml.bind.JAXBException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs.UserPrefsView;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class RunExternalAction implements IWorkbenchWindowActionDelegate {

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
			if (dialog.open() == RunApplicationDialog.OK) {
				String url = dialog.getUrl();
				ApplicationInformation appInfo = OpenSocialUtil.createApplicationInformation(url);
				ApplicationService applicationService = Activator.getDefault().getApplicationService();
				applicationService.storeAppInfo(appInfo);
				String view = dialog.getView();
				String viewer = dialog.getViewer();
				String owner = dialog.getOwner();
				String width = dialog.getWidth();
				String appId = appInfo.getAppId();
				boolean useExternalBrowser = dialog.isUseExternalBrowser();
				String country = dialog.getCountry();
				String language = dialog.getLanguage();
				LaunchApplicationInformation information = new LaunchApplicationInformation(
						viewer, owner, view, width, appId, useExternalBrowser,
						country, language, null, url, appInfo.getModule().getModulePrefs().getTitle());
				Job job = new LaunchApplicationJob("Running application", information, shell);
				job.schedule();
				notifyUserPrefsView(information);
			}
		} catch(ConnectionException e) {
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
		} catch (JAXBException e) {
			e.printStackTrace();
			Throwable ex = e.getLinkedException() != null ? e.getLinkedException() : e;
			MessageDialog.openError(shell, "Error", "Invalid gadget file.\n" + ex.getMessage());
		} catch (CoreException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		}
	}
	
	private void notifyUserPrefsView(final LaunchApplicationInformation information) {
		final IWorkbenchWindow window = targetPart.getSite().getWorkbenchWindow();
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					UserPrefsView userPrefsView;
					userPrefsView = (UserPrefsView)window.getActivePage().showView(UserPrefsView.ID);
					userPrefsView.showUserPrefFields(information, information.getUrl());
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
