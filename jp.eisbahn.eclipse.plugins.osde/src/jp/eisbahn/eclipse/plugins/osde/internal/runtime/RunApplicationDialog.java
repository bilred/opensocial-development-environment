package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class RunApplicationDialog extends TitleAreaDialog {

	private List<Person> people;
	
	private String view;
	private String owner;
	private String viewer;
	private String width;
	
	private Combo viewKind;

	private Combo owners;

	private Combo viewers;

	private Spinner widths;
	
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
		layout.numColumns = 4;
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
		viewKind.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				switch(viewKind.getSelectionIndex()) {
				case 0:
					widths.setSelection(800);
					break;
				case 1:
				case 2:
					widths.setSelection(500);
					break;
				case 3:
					widths.setSelection(600);
					break;
				}
			}
		});
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Width:");
		widths = new Spinner(panel, SWT.BORDER);
		widths.setMaximum(1000);
		widths.setMinimum(200);
		widths.setSelection(800);
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
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
		viewer = viewers.getItem(viewers.getSelectionIndex());
		owner = owners.getItem(owners.getSelectionIndex());
		width = widths.getText();
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
	
	public String getWidth() {
		return width;
	}

}
