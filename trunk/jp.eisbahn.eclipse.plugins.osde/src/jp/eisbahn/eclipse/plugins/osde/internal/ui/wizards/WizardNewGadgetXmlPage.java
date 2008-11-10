package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 新規に作成するOpenSocialアプリケーションの各種情報を定義するためのウィザードページです。
 * @author yoichiro
 */
public class WizardNewGadgetXmlPage extends WizardPage {

	/**
	 * このオブジェクトが生成されるときに呼び出されます。
	 * @param pageName ページの名前
	 */
	public WizardNewGadgetXmlPage(String pageName) {
		super(pageName);
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
		// Informationグループ
		Group infoGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		infoGroup.setText("Application information");
		infoGroup.setFont(parent.getFont());
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		infoGroup.setLayout(layout);
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Titleラベル
		createLabel(infoGroup, "Title:");
		// Title入力フィールド
		Text titleText = createText(infoGroup);
		// Title URLラベル
		createLabel(infoGroup, "Title URL:");
		// Title URL入力フィールド
		Text titleUrlText = createText(infoGroup);
		// Descriptionラベル
		createLabel(infoGroup, "Description:");
		// Description入力エリア
		Text descriptionText = new Text(infoGroup, SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		layoutData.heightHint = 50;
		descriptionText.setLayoutData(layoutData);
		descriptionText.setFont(parent.getFont());
		// Authorラベル
		createLabel(infoGroup, "Author:");
		// Author入力フィールド
		Text authorText = createText(infoGroup);
		// Author Emailラベル
		createLabel(infoGroup, "Author Email:");
		// Author Email入力フィールド
		Text authorEmailText = createText(infoGroup);
		// Screenshotラベル
		createLabel(infoGroup, "Screen Shot:");
		// Screenshot入力フィールド
		Text screenshotText = createText(infoGroup);
		// Thumbnailラベル
		createLabel(infoGroup, "Thumbnail:");
		// Thumbnail入力フィールド
		Text thumbnailText = createText(infoGroup);
	}
	
	/**
	 * 指定された文字列を表示するためのラベルを生成します。
	 * @param parent ラベルを配置するコンテナ
	 * @param text 表示文字列
	 */
	private void createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.BEGINNING;
		layoutData.verticalIndent = 7;
		label.setLayoutData(layoutData);
		label.setFont(parent.getFont());
	}
	
	/**
	 * 入力フィールドを生成し、その結果を返します。
	 * @param parent 入力フィールドを配置するコンテナ
	 * @return 生成された入力フィールド
	 */
	private Text createText(Composite parent) {
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setFont(parent.getFont());
		return text;
	}

}
