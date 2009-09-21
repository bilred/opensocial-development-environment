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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.io.IOUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.xml.sax.SAXException;

public abstract class AbstractRunAction {
	
	protected Shell shell;
	protected IWorkbenchPart targetPart;

	protected void notifyUserPrefsView(LaunchApplicationInformation information) {
		UserPrefsViewNotifier.fire(information, shell, targetPart);
	}
	
	public void init(IWorkbenchWindow window) {
		targetPart = window.getActivePage().getActivePart();
		shell = targetPart.getSite().getShell();
	}
	
	protected void launch(IFile gadgetXmlFile, IProject project) {
		try {
			ApplicationInformation appInfo = OpenSocialUtil.createApplicationInformation(gadgetXmlFile);
			ApplicationService applicationService = Activator.getDefault().getApplicationService();
			applicationService.storeAppInfo(appInfo);
			//
			PersonService personService = Activator.getDefault().getPersonService();
			List<Person> people = personService.getPeople();
			RunApplicationDialog dialog = new RunApplicationDialog(shell, people, gadgetXmlFile);
			if (dialog.open() == RunApplicationDialog.OK) {
				Job job = new CreateWebContextJob("Create web context", project);
				job.schedule();
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
						country, language, project, gadgetXmlFile.getProjectRelativePath().toPortableString(),
						project.getName() + ":" + gadgetXmlFile.getName());
				job = new LaunchApplicationJob("Running application", information, shell);
				job.schedule(1000);
				notifyUserPrefsView(information);
				Activator.getDefault().setLastApplicationInformation(information);
			}
		} catch(ConnectionException e) {
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		} catch (IOException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		} catch (SAXException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		}
	}

	private class CreateWebContextJob extends Job {
		
		private IProject project;

		private CreateWebContextJob(String name, IProject project) {
			super(name);
			this.project = project;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			FileOutputStream fos = null;
			try {
				InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/shindig/context.tmpl"), "UTF-8");
				StringWriter out = new StringWriter();
				IOUtils.copy(in, out);
				String code = out.toString();
				code = code.replace("$project_name$", project.getName());
				IPath location = project.getFolder("target").getLocation();
				code = code.replace("$context_dir$", location.toOSString());
				String jettyDir = Activator.getDefault().getOsdeConfiguration().getJettyDir();
				File jettyDirFile = new File(jettyDir);
				if (!jettyDirFile.isDirectory()) {
					jettyDirFile.mkdirs();
				}
				File file = new File(jettyDirFile, "osde_context_" + project.getName() + ".xml");
				fos = new FileOutputStream(file);
				ByteArrayInputStream bytes = new ByteArrayInputStream(code.getBytes("UTF-8"));
				IOUtils.copy(bytes, fos);
				return Status.OK_STATUS;
			} catch(IOException e) {
				// TODO
				e.printStackTrace();
				throw new IllegalStateException(e);
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
		
	}

}
