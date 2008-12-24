package jp.eisbahn.eclipse.plugins.osde.internal.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class NewPersonDialog extends TitleAreaDialog {

	public NewPersonDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create new person");
		setMessage("Please input Person ID and display name.");
		Composite composite = (Composite)super.createDialogArea(parent);
		
		return composite;
	}

}
