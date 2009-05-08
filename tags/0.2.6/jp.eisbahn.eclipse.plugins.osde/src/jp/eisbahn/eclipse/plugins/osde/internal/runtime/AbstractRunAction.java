package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs.UserPrefsView;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public abstract class AbstractRunAction {
	
	protected Shell shell;
	protected IWorkbenchPart targetPart;

	protected void notifyUserPrefsView(final LaunchApplicationInformation information) {
		IProject project = information.getProject();
		final String url = "http://localhost:8080/" + project.getName().replace(" ", "%20") + "/" + information.getUrl().replace(" ", "%20");
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
					throw new IllegalStateException(e);
				}
			}
		});
	}
	
	public void init(IWorkbenchWindow window) {
		targetPart = window.getActivePage().getActivePart();
		shell = targetPart.getSite().getShell();
	}

}
