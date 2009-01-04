package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;
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

import com.google.gadgets.Module;
import com.google.gadgets.ViewType;
import com.google.gadgets.Module.Content;

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
								JAXBContext context = JAXBContext.newInstance(Module.class);
								Unmarshaller um = context.createUnmarshaller();
								Module module = (Module)um.unmarshal(orgFile.getContents());
								List<Content> contents = module.getContent();
								for (Content content : contents) {
									if (ViewType.html.toString().equals(content.getType())) {
										String value = content.getValue();
										int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
										Pattern pattern = Pattern.compile("http://localhost:" + port + "/");
										Matcher matcher = pattern.matcher(value);
										StringBuffer sb = new StringBuffer();
										while(matcher.find()) {
											matcher.appendReplacement(sb, url);
										}
										matcher.appendTail(sb);
										content.setValue(sb.toString());
									}
								}
								Marshaller ma = context.createMarshaller();
								ByteArrayOutputStream buf = new ByteArrayOutputStream();
								ma.marshal(module, buf);
								ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
								IOUtils.copy(in, out);
							} catch(JAXBException e) {
								e.printStackTrace();
							} catch(CoreException e) {
								e.printStackTrace();
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
