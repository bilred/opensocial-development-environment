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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.StatusUtil;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gadgets.GadgetXmlSerializer;
import com.google.gadgets.model.Module;
import com.google.gadgets.ViewType;
import com.google.gadgets.model.Module.Content;
import com.google.gadgets.parser.IParser;
import com.google.gadgets.parser.ParserException;
import com.google.gadgets.parser.ParserFactory;
import com.google.gadgets.parser.ParserType;

public class OpenSocialApplicationExportWizard extends Wizard implements IExportWizard {

	private WizardOpenSocialApplicationExportPage page;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Export OpenSocial application");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		page = new WizardOpenSocialApplicationExportPage("exportPage");
		page.setTitle("Export OpenSocial application");
		page.setDescription("Export OpenSocial application which can deploy to your web server.");
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		export();
		return true;
	}

	private void export() {
		final IProject project = page.getProject();
		final String url = page.getUrl();
		final String output = page.getOutput();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				ZipOutputStream out = null;
				try {
					ResourceCounter resourceCounter = new ResourceCounter();
					project.accept(resourceCounter);
					int fileCount = resourceCounter.getFileCount();
					monitor.beginTask("Exporting application", fileCount);
					out = new ZipOutputStream(new FileOutputStream(new File(output)));
					project.accept(new ApplicationExporter(out, project, url, monitor));
					monitor.done();
				} catch(CoreException e) {
					throw new InvocationTargetException(e);
				} catch(IOException e) {
					throw new InvocationTargetException(e);
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch(IOException e) {
						}
					}
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
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(cause.getStatus().getSeverity(), "Error occurd at exporting application.", cause));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurd at exporting application.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(IStatus.WARNING, "Error occurd at exporting application.", t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurd at exporting application.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
		}
	}
	
	private class ApplicationExporter implements IResourceVisitor {
		
		private ZipOutputStream out;
		private IProject project;
		private String url;
		private IProgressMonitor monitor;
		
		public ApplicationExporter(ZipOutputStream out, IProject project, String url, IProgressMonitor monitor) {
			super();
			this.out = out;
			this.project = project;
			this.monitor = monitor;
			this.url = url;
		}
		
		public boolean visit(IResource resource) throws CoreException {
			try {
				int type = resource.getType();
				switch(type) {
				case IResource.FILE:
					IFile orgFile = (IFile)resource;
					if (!orgFile.getName().equals(".project")) {
						ZipEntry entry = new ZipEntry(resource.getProjectRelativePath().toPortableString());
						out.putNextEntry(entry);
						if (OpenSocialUtil.isGadgetXml(orgFile)) {
							try {
								IParser parser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
								Module module = null;
								module = (Module) parser.parse(orgFile.getContents());
								List<Content> contents = module.getContent();
								for (Content content : contents) {
									if (ViewType.html.toString().equals(content.getType())) {
										String value = content.getValue();
										Pattern pattern = Pattern.compile("http://localhost:8080/" + project.getName() + "/");
										Matcher matcher = pattern.matcher(value);
										StringBuffer sb = new StringBuffer();
										while(matcher.find()) {
											matcher.appendReplacement(sb, url);
										}
										matcher.appendTail(sb);
										content.setValue(sb.toString());
									}
								}
								String serialize = GadgetXmlSerializer.serialize(module);
								ByteArrayInputStream in = new ByteArrayInputStream(serialize.getBytes("UTF-8"));
								IOUtils.copy(in, out);
							} catch(ParserException e) {
								Logging.error("Exporting/Parsing the project files failed.", e);
							}
						} else {
							IOUtils.copy(orgFile.getContents(), out);
						}
						out.closeEntry();
						monitor.worked(1);
					}
					return false;
				case IResource.FOLDER:
					if (resource.isDerived()) {
						return false;
					} else {
						ZipEntry entry = new ZipEntry(resource.getProjectRelativePath().toPortableString() + "/");
						out.putNextEntry(entry);
						out.closeEntry();
						monitor.worked(1);
						return true;
					}
				default:
					return true;
				}
			} catch(IOException e) {
				throw new CoreException(StatusUtil.newStatus(IStatus.ERROR, e.getMessage(), e));
			}
		}
	}

	private class ResourceCounter implements IResourceVisitor {
		
		private int fileCount = 0;
		
		public boolean visit(IResource resource) throws CoreException {
			int type = resource.getType();
			switch(type) {
			case IResource.FILE:
				fileCount++;
				return false;
			case IResource.FOLDER:
				if (resource.isDerived()) {
					return false;
				} else {
					fileCount++;
					return true;
				}
			default:
				return true;
			}
		}
		
		public int getFileCount() {
			return fileCount;
		}
		
	}

}
