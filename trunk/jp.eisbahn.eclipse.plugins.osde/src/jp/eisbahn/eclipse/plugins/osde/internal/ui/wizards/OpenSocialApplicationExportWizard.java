package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class OpenSocialApplicationExportWizard extends Wizard implements IExportWizard {

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Export OpenSocial application");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		WizardOpenSocialApplicationExportPage page = new WizardOpenSocialApplicationExportPage("exportPage");
		page.setTitle("Export OpenSocial application");
		page.setDescription("Export OpenSocial application which can deploy to your web server.");
		addPage(page);
	}
	
}
