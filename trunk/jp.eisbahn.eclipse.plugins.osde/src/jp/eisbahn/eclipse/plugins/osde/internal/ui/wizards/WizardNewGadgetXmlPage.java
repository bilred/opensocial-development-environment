package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 新規に作成するOpenSocialアプリケーションの各種情報を定義するためのウィザードページです。
 * @author yoichiro
 */
public class WizardNewGadgetXmlPage extends WizardPage {

	/** Title入力フィールド */
	private Text titleText;
	/** Title URL入力フィールド */
	private Text titleUrlText;
	/** Description入力エリア */
	private Text descriptionText;
	/** Author入力フィールド */
	private Text authorText;
	/** Author Email入力フィールド */
	private Text authorEmailText;
	/** Screen Shot入力フィールド */
	private Text screenshotText;
	/** Thumbnail入力フィールド */
	private Text thumbnailText;
	/** opensocial-0.8チェックボックス */
	private Button opensocial08Button;
	/** opensocial-0.7チェックボックス */
	private Button opensocial07Button;
	/** PubSubチェックボックス */
	private Button pubsubButton;
	/** Viewsチェックボックス */
	private Button viewsButton;
	/** Flashチェックボックス */
	private Button flashButton;
	/** Skinsチェックボックス */
	private Button skinsButton;
	/** Dynamic Heightチェックボックス */
	private Button dynamicHeightButton;
	/** Set Titleチェックボックス */
	private Button setTitleButton;
	/** Mini Messageチェックボックス */
	private Button miniMessageButton;
	/** Tabsチェックボックス */
	private Button tabsButton;
	/** Canvasチェックボックス */
	private Button canvasButton;
	/** Profileチェックボックス */
	private Button profileButton;
	/** Previewチェックボックス */
	private Button previewButton;
	/** Homeチェックボックス */
	private Button homeButton;
	
	/** 入力値の変更を検知するリスナオブジェクト */
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			boolean valid = validatePage();
			setPageComplete(valid);
		}
	};

	/**
	 * このオブジェクトが生成されるときに呼び出されます。
	 * @param pageName ページの名前
	 */
	public WizardNewGadgetXmlPage(String pageName) {
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
		createFeaturesControls(composite);
		createViewsControls(composite);
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
		Group infoGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		infoGroup.setText("Application information");
		infoGroup.setFont(parent.getFont());
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		infoGroup.setLayout(layout);
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createLabel(infoGroup, "Title:");
		titleText = createText(infoGroup);
		titleText.addListener(SWT.Modify, modifyListener);
		createLabel(infoGroup, "Title URL:");
		titleUrlText = createText(infoGroup);
		createLabel(infoGroup, "Description:");
		descriptionText = new Text(infoGroup, SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		layoutData.heightHint = 50;
		descriptionText.setLayoutData(layoutData);
		descriptionText.setFont(parent.getFont());
		createLabel(infoGroup, "Author:");
		authorText = createText(infoGroup);
		createLabel(infoGroup, "Author Email:");
		authorEmailText = createText(infoGroup);
		authorEmailText.addListener(SWT.Modify, modifyListener);
		createLabel(infoGroup, "Screen Shot:");
		screenshotText = createText(infoGroup);
		createLabel(infoGroup, "Thumbnail:");
		thumbnailText = createText(infoGroup);
	}
	
	/**
	 * Featuresにて定義される情報の入力UIを構築します。
	 * @param parent 親のコンテナ
	 */
	private void createFeaturesControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group featuresGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		featuresGroup.setText("Features");
		featuresGroup.setFont(parent.getFont());
		featuresGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		featuresGroup.setLayout(layout);
		featuresGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		opensocial08Button = createCheckbox(featuresGroup, "OpenSocial v0.8");
		opensocial08Button.setSelection(true);
		opensocial07Button = createCheckbox(featuresGroup, "OpenSocial v0.7");
		pubsubButton = createCheckbox(featuresGroup, "PubSub");
		viewsButton = createCheckbox(featuresGroup, "Views");
		flashButton = createCheckbox(featuresGroup, "Flash");
		skinsButton = createCheckbox(featuresGroup, "Skins");
		dynamicHeightButton = createCheckbox(featuresGroup, "Dynamic Height");
		setTitleButton = createCheckbox(featuresGroup, "Set Title");
		miniMessageButton = createCheckbox(featuresGroup, "Mini Message");
		tabsButton = createCheckbox(featuresGroup, "Tabs");
	}
	
	/**
	 * サポートするViewsを定義するUIを構築します。
	 * @param parent 親のコンテナ
	 */
	private void createViewsControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group viewsGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		viewsGroup.setText("Views");
		viewsGroup.setFont(parent.getFont());
		viewsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		viewsGroup.setLayout(layout);
		viewsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		canvasButton = createCheckbox(viewsGroup, "Canvas");
		canvasButton.addListener(SWT.Selection, modifyListener);
		canvasButton.setSelection(true);
		profileButton = createCheckbox(viewsGroup, "Profile");
		profileButton.addListener(SWT.Selection, modifyListener);
		profileButton.setSelection(true);
		previewButton = createCheckbox(viewsGroup, "Preview");
		previewButton.addListener(SWT.Selection, modifyListener);
		homeButton = createCheckbox(viewsGroup, "Home");
		homeButton.addListener(SWT.Selection, modifyListener);
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

	/**
	 * チェックボックスを生成し、その結果を返します。
	 * @param parent チェックボックスを配置するコンテナ
	 * @param text 表示文字列
	 * @return 生成されたチェックボックス
	 */
	private Button createCheckbox(Composite parent, String text) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setFont(parent.getFont());
		return button;
	}
	
	private boolean validatePage() {
		String title = titleText.getText().trim();
		if (title.length() == 0) {
			setErrorMessage(null);
			setMessage("Title is empty.");
			return false;
		}
		String authorEmail = authorEmailText.getText().trim();
		if (authorEmail.length() == 0) {
			setErrorMessage(null);
			setMessage("Author Email is empty.");
			return false;
		}
		boolean canvas = canvasButton.getSelection();
		boolean profile = profileButton.getSelection();
		boolean preview = previewButton.getSelection();
		boolean home = homeButton.getSelection();
		if (!(canvas || profile || preview || home)) {
			setErrorMessage(null);
			setMessage("Not checked any views.");
			return false;
		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/**
	 * 入力された情報を持つオブジェクトを返します。
	 * @return 入力された情報を持つオブジェクト
	 */
	public GadgetXmlData getGadgetXmlData() {
		GadgetXmlData data = new GadgetXmlData();
		data.setAuthor(authorText.getText().trim());
		data.setAuthorEmail(authorEmailText.getText().trim());
		data.setCanvas(canvasButton.getSelection());
		data.setDescription(descriptionText.getText().trim());
		data.setDynamicHeight(dynamicHeightButton.getSelection());
		data.setFlash(flashButton.getSelection());
		data.setHome(homeButton.getSelection());
		data.setMiniMessage(miniMessageButton.getSelection());
		data.setOpensocial07(opensocial07Button.getSelection());
		data.setOpensocial08(opensocial08Button.getSelection());
		data.setPreview(previewButton.getSelection());
		data.setProfile(profileButton.getSelection());
		data.setPubsub(pubsubButton.getSelection());
		data.setScreenshot(screenshotText.getText().trim());
		data.setSetTitle(setTitleButton.getSelection());
		data.setSkins(skinsButton.getSelection());
		data.setTabs(tabsButton.getSelection());
		data.setThumbnail(thumbnailText.getText().trim());
		data.setTitle(titleText.getText().trim());
		data.setTitleUrl(titleUrlText.getText().trim());
		data.setViews(viewsButton.getSelection());
		return data;
	}

}
