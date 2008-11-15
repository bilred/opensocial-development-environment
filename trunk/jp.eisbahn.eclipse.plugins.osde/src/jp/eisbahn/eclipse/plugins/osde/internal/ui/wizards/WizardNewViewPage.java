package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createCheckbox;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createText;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class WizardNewViewPage extends WizardPage {
	
	public WizardNewViewPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		TabFolder tabFolder = new TabFolder(composite, SWT.NULL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createViewControls(composite, "Canvas", tabFolder);
		createViewControls(composite, "Profile", tabFolder);
		createViewControls(composite, "Preview", tabFolder);
		createViewControls(composite, "Home", tabFolder);
		//
//		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}
	
	private void createViewControls(Composite composite, String name, TabFolder folder) {
		TabItem item = new TabItem(folder, SWT.BORDER);
		item.setText(name);
		item.setControl(createEachViewControls(folder));
	}

	private Composite createEachViewControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button htmlButton = createRadio(composite, "Type: HTML");
		Group htmlGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		htmlGroup.setFont(parent.getFont());
		htmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 1;
		htmlGroup.setLayout(layout);
		htmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button createJavaScriptFileButton = createCheckbox(htmlGroup, "Create the external JavaScript file for this canvas view.");
		final Button initFunctionButton = createCheckbox(htmlGroup, "Generate the init() function that is called when this canvas view is loaded.");
		createJavaScriptFileButton.setEnabled(false);
		initFunctionButton.setEnabled(false);
		createJavaScriptFileButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				boolean selection = createJavaScriptFileButton.getSelection();
				initFunctionButton.setEnabled(selection);
			}
		});
		final Button urlButton = createRadio(composite, "Type: URL");
		Group urlGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		urlGroup.setFont(parent.getFont());
		urlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 2;
		urlGroup.setLayout(layout);
		urlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createLabel(urlGroup, "Location URL:");
		final Text hrefText = createText(urlGroup);
		hrefText.setEnabled(false);
		Button notSupportButton = createRadio(composite, "Not support");
		SelectionListener selectionListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				createJavaScriptFileButton.setEnabled(htmlButton.getSelection());
				initFunctionButton.setEnabled(htmlButton.getSelection() & createJavaScriptFileButton.getSelection());
				hrefText.setEnabled(urlButton.getSelection());
			}
		};
		htmlButton.addSelectionListener(selectionListener);
		urlButton.addSelectionListener(selectionListener);
		notSupportButton.addSelectionListener(selectionListener);
		notSupportButton.setSelection(true);
		return composite;
	}

}
