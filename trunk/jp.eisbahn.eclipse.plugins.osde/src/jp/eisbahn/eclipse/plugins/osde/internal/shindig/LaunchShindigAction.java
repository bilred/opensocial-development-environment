package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import jp.eisbahn.eclipse.plugins.osde.internal.views.PersonView;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class LaunchShindigAction implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;
	
	/**
	 * Constructor for Action1.
	 */
	public LaunchShindigAction() {
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
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			// launch shindig & database
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Shindig Database")) {
					final ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							try {
								DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Apache Shindig")) {
					final ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
					shell.getDisplay().timerExec(3000, new Runnable() {
						public void run() {
							try {
								DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
								PersonView personView = (PersonView)targetPart.getSite().getPage().showView(PersonView.ID);
								personView.connect();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
