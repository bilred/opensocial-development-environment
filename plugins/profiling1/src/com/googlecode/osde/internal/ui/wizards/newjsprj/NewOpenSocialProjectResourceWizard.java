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
package com.googlecode.osde.internal.ui.wizards.newjsprj;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.EnumMap;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeProjectNature;
import com.googlecode.osde.internal.editors.GadgetXmlEditor;
import com.googlecode.osde.internal.utils.StatusUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.google.gadgets.ViewName;

public class NewOpenSocialProjectResourceWizard extends BasicNewProjectResourceWizard {

	public static final String WIZARD_ID = "com.googlecode.osde.newWizards.NewOpenSocialProjectWizard";
	
	private  WizardNewProjectCreationPage mainPage;
	
	private WizardNewGadgetXmlPage gadgetXmlPage;
	
	private WizardNewViewPage viewPage;
	
	private IProject newProject;
	
	public NewOpenSocialProjectResourceWizard() {
		super();
		IDialogSettings workbenchSetting = Activator.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSetting.getSection("NewOpenSocialProjectResourceWizard");
		if (section == null) {
			section = workbenchSetting.addNewSection("NewOpenSocialProjectResourceWizard");
		}
		setDialogSettings(section);
	}
	
	public void addPages() {
//		super.addPages();
		//
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("OpenSocial Project");
		mainPage.setDescription("Create a new OpenSocial project resource.");
		addPage(mainPage);
		//
		gadgetXmlPage = new WizardNewGadgetXmlPage("newGadgetXmlPage");
		gadgetXmlPage.setTitle("Application settings");
		gadgetXmlPage.setDescription("Define this application settings.");
		addPage(gadgetXmlPage);
		//
		viewPage = new WizardNewViewPage("newViewPage");
		viewPage.setTitle("View settings");
		viewPage.setDescription("Define the view settings.");
		addPage(viewPage);
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
//		final IProject newProjectHandle = mainPage.getProjectHandle();
		String projectName = mainPage.getProjectName();
		URI location = null;
		if (!mainPage.useDefaults()) {
			location = mainPage.getLocationURI();
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject newProjectHandle = workspace.getRoot().getProject(projectName);
		final IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocationURI(location);
		final GadgetXmlData gadgetXmlData = gadgetXmlPage.getInputtedData();
		final EnumMap<ViewName, GadgetViewData> gadgetViewData = viewPage.getInputedData();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					newProjectHandle.create(description, monitor);
					newProjectHandle.open(monitor);
					IProjectDescription description = newProjectHandle.getDescription();
					if (!description.hasNature(OsdeProjectNature.ID)) {
						String[] ids = description.getNatureIds();
						String[] newIds = new String[ids.length + 1];
						System.arraycopy(ids, 0, newIds, 0, ids.length);
						newIds[ids.length] = OsdeProjectNature.ID;
						description.setNatureIds(newIds);
						newProjectHandle.setDescription(description, monitor);
					}
					final IFile gadgetXmlFile = (new GadgetXmlFileGenerator(newProjectHandle, gadgetXmlData, gadgetViewData)).generate(monitor);
					(new JavaScriptFileGenerator(newProjectHandle, gadgetXmlData, gadgetViewData)).generate(monitor);
					monitor.beginTask("Opening the Gadget XML file.", 1);
					getShell().getDisplay().syncExec(new Runnable() {
						public void run() {
							IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
							try {
								if (dw != null) {
									IWorkbenchPage page = dw.getActivePage();
									if (page != null) {
										IDE.openEditor(page, gadgetXmlFile, GadgetXmlEditor.ID, true);
									}
								}
							} catch (PartInitException e) {
								throw new RuntimeException(e);
							}
						}
					});
					monitor.worked(1);
					monitor.done();
				} catch(CoreException e) {
					throw new InvocationTargetException(e);
				} catch(UnsupportedEncodingException e) {
					throw new InvocationTargetException(e);
				} catch(IOException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
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

}
