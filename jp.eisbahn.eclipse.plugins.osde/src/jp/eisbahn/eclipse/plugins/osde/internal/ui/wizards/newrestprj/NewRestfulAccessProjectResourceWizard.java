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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newrestprj;

import java.lang.reflect.InvocationTargetException;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.StatusUtil;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewRestfulAccessProjectResourceWizard extends BasicNewProjectResourceWizard {

	public static final String WIZARD_ID = "jp.eisbahn.eclipse.plugins.osde.newWizards.NewRestfulAccessProjectResourceWizard";
	
	private WizardNewProjectCreationPage mainPage;
	
	private WizardNewRestfulAccessProjectPage restfulPage;
	
	private IProject newProject;

	private ApplicationImpl application;

	private Person person;
	
	public NewRestfulAccessProjectResourceWizard() {
		super();
	}
	
	public void addPages() {
//		super.addPages();
		//
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("OpenSocial Restful Project");
		mainPage.setDescription("Create a new OpenSocial project with Restful Protocol.");
		addPage(mainPage);
		//
		restfulPage = new WizardNewRestfulAccessProjectPage("newRestfulAccessProjectPage");
		restfulPage.setTitle("OpenSocial Restful Project");
		restfulPage.setDescription("Create a new OpenSocial project with Restful Protocol.");
		addPage(restfulPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle("New OpenSocial project");
	}
	
	public IProject getNewProject() {
		return newProject;
	}

	@Override
	public boolean performFinish() {
		createNewProject();
		if (newProject == null) {
			return false;
		}
		updatePerspective();
		selectAndReveal(newProject);
		return true;
	}
	
	private IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}
		final IProject newProjectHandle = mainPage.getProjectHandle();
		final String srcFolderName = restfulPage.getSourceDirectoryName();
		final String binFolderName = restfulPage.getBinaryDirectoryName();
		final String libFolderName = restfulPage.getLibraryDirectoryName();
		IRunnableWithProgress op = new RestfulAccessProjectFactory(
				srcFolderName, libFolderName, newProjectHandle, binFolderName,
				application, person, getWorkbench());
		try {
			getContainer().run(true, true, op);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException)t.getCause();
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(cause.getStatus().getSeverity(), "Error occurred when creating project.", cause));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurred when creating project.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(IStatus.WARNING, "Error occurred when creating project.", t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurred when creating project.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
			return null;
		}
		newProject = newProjectHandle;
		return newProject;
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
		super.setInitializationData(config, propertyName, data);
	}

	public void setApplication(ApplicationImpl application) {
		this.application = application;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
