package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class RunAgainAction extends AbstractRunAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void run(IAction action) {
		LaunchApplicationInformation information = Activator.getDefault().getLastApplicationInformation();
		if (information == null) {
			MessageDialog.openWarning(shell, "Warning", "Any application is not started yet.");
		} else {
			Job job = new LaunchApplicationJob("Running application", information, shell);
			job.schedule();
			notifyUserPrefsView(information);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
