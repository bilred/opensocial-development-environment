package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class LaunchWebServerAction implements IObjectActionDelegate {

	private IProject project;
	
	/**
	 * Constructor for Action1.
	 */
	public LaunchWebServerAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Job job = new Job("Starting web server") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(createLauncherName(project), 1);
					monitor.subTask("Starting Web server.");
					ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
					ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
					ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
					for (int i = 0; i < configurations.length; i++) {
						if (configurations[i].getName().equals(createLauncherName(project))) {
							ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
							DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
						}
					}
					monitor.worked(1);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	private String createLauncherName(IProject project) {
		return "Web server for " + project.getName();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection)selection;
			Object element = structured.getFirstElement();
			if (element instanceof IProject) {
				project = (IProject)element;
			}
		}
	}

}
