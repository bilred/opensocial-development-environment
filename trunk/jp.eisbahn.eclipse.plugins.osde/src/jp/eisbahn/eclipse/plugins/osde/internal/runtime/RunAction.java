package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.bind.JAXBException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class RunAction implements IObjectActionDelegate {

	private Shell shell;
	private IFile gadgetXmlFile;
	private IProject project;
	
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
			RunApplicationDialog dialog = new RunApplicationDialog(shell, people);
			if (dialog.open() == RunApplicationDialog.OK) {
				String view = dialog.getView();
				String viewer = dialog.getViewer();
				String owner = dialog.getOwner();
				String width = dialog.getWidth();
				Job job = new LaunchApplicationJob("Running application", viewer, owner, view, width);
				job.setUser(true);
				job.schedule();
			}
		} catch(ConnectionException e) {
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
		} catch (JAXBException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
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

	private class LaunchApplicationJob extends Job {
		private String viewer;
		private String owner;
		private String view;
		private String width;

		private LaunchApplicationJob(String name, String viewer, String owner, String view, String width) {
			super(name);
			this.viewer = viewer;
			this.owner = owner;
			this.view = view;
			this.width = width;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				monitor.beginTask("Running application", 1);
				int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
				final String url = "http://localhost:8080/gadgets/files/osdecontainer/index.html?url=http://localhost:" + port + "/"
						+ gadgetXmlFile.getName()
						+ "&view=" + view
						+ "&viewerId=" + URLEncoder.encode(viewer, "UTF-8")
						+ "&ownerId=" + URLEncoder.encode(owner, "UTF-8")
						+ "&width=" + URLEncoder.encode(width, "UTF-8");
				shell.getDisplay().syncExec(new Runnable() {
					public void run() {
						try {
							IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
							if (support.isInternalWebBrowserAvailable()) {
								IWebBrowser browser = support.createBrowser(
										IWorkbenchBrowserSupport.LOCATION_BAR 
											| IWorkbenchBrowserSupport.NAVIGATION_BAR
											| IWorkbenchBrowserSupport.AS_EDITOR,
										url, project.getName(), project.getName());
								browser.openURL(new URL(url));
							}
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				monitor.worked(1);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

}
