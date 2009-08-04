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
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class RunAction implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {

	private Shell shell;
	IFile gadgetXmlFile;
	IProject project;
	private IWorkbenchPart targetPart;
	
	/**
	 * Constructor for Action1.
	 */
	public RunAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			ApplicationInformation appInfo = OpenSocialUtil.createApplicationId(gadgetXmlFile);
			ApplicationService applicationService = Activator.getDefault().getApplicationService();
			applicationService.storeAppInfo(appInfo);
			//
			PersonService personService = Activator.getDefault().getPersonService();
			List<Person> people = personService.getPeople();
			RunApplicationDialog dialog = new RunApplicationDialog(shell, people, gadgetXmlFile);
			if (dialog.open() == RunApplicationDialog.OK) {
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
						country, language, project, gadgetXmlFile.getName());
				Job job = new LaunchApplicationJob("Running application", information, shell);
				job.schedule();
				notifyUserPrefsView(information);
			}
		} catch(ConnectionException e) {
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
		} catch (JAXBException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		}
	}
	
	private void notifyUserPrefsView(final LaunchApplicationInformation information) {
		try {
			int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
			final String url = "http://localhost:" + port + "/" + gadgetXmlFile.getName();
			final IWorkbenchWindow window = targetPart.getSite().getWorkbenchWindow();
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						UserPrefsView userPrefsView;
						userPrefsView = (UserPrefsView)window.getActivePage().showView(UserPrefsView.ID);
						userPrefsView.showUserPrefFields(information, url);
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection)selection;
			Object element = structured.getFirstElement();
			if (element instanceof IFile) {
				gadgetXmlFile = (IFile)element;
				project = gadgetXmlFile.getProject();
			}
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		targetPart = window.getActivePage().getActivePart();
		shell = targetPart.getSite().getShell();
	}

}