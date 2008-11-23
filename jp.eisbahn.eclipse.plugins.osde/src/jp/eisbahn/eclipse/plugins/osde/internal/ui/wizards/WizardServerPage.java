package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;

/**
 * アプリケーションの実行サーバに関するウィザードページです。
 * @author yoichiro
 */
public class WizardServerPage extends WizardPage {

	private Spinner portSpinner;

	/**
	 * このオブジェクトが生成されるときに呼び出されます。
	 * @param pageName ページの名前
	 */
	public WizardServerPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	/**
	 * このウィザードページのUIを構築します。
	 * @param parent 親のコンテナ
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createModulePrefsControls(composite);
		//
		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	/**
	 * ModulePrefsにて定義される情報の入力UIを構築します。
	 * @param parent 親のコンテナ
	 */
	private void createModulePrefsControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group localGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		localGroup.setText("Local environment for testing");
		localGroup.setFont(parent.getFont());
		localGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 2;
		localGroup.setLayout(layout);
		localGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createLabel(localGroup, "Port:");
		portSpinner = new Spinner(localGroup, SWT.BORDER);
		portSpinner.setMinimum(1024);
		portSpinner.setMaximum(65535);
		portSpinner.setSelection(8081);
	}
	
	private boolean validatePage() {
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/**
	 * 入力された情報を持つオブジェクトを返します。
	 * @return 入力された情報を持つオブジェクト
	 */
	public ServerData getInputedData() {
		ServerData data = new ServerData();
		data.setLocalPort(portSpinner.getSelection());
		return data;
	}

}
