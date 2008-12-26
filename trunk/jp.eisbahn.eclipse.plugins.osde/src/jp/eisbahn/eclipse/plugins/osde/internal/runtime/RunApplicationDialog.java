package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class RunApplicationDialog extends TitleAreaDialog {

	private List<Person> people;
	
	private String view;
	private String owner;
	private String viewer;
	
	private Combo viewKind;

	private Combo owners;

	private Combo viewers;
	
	public RunApplicationDialog(Shell shell, List<Person> people) {
		super(shell);
		this.people = people;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Run application");
		setMessage("Please select some information.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("View:");
		viewKind = new Combo(panel, SWT.READ_ONLY);
		viewKind.add("Canvas");
		viewKind.add("Profile");
		viewKind.add("Home");
		viewKind.add("Preview");
		viewKind.select(0);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Owner:");
		owners = new Combo(panel, SWT.READ_ONLY);
		for (Person person : people) {
			owners.add(person.getId());
		}
		owners.select(0);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Viewer:");
		viewers = new Combo(panel, SWT.READ_ONLY);
		for (Person person : people) {
			viewers.add(person.getId());
		}
		viewers.select(0);
		//
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
		viewer = viewers.getItem(viewers.getSelectionIndex());
		owner = owners.getItem(owners.getSelectionIndex());
		setReturnCode(OK);
		close();
	}

	public String getView() {
		return view;
	}

	public String getOwner() {
		return owner;
	}

	public String getViewer() {
		return viewer;
	}

}
