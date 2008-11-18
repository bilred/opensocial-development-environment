package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ComponentUtils {

	/**
	 * 指定された文字列を表示するためのラベルを生成します。
	 * @param parent ラベルを配置するコンテナ
	 * @param text 表示文字列
	 */
	public static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.BEGINNING;
		layoutData.verticalIndent = 7;
		label.setLayoutData(layoutData);
		label.setFont(parent.getFont());
		return label;
	}
	
	/**
	 * 入力フィールドを生成し、その結果を返します。
	 * @param parent 入力フィールドを配置するコンテナ
	 * @return 生成された入力フィールド
	 */
	public static Text createText(Composite parent) {
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
	public static Button createCheckbox(Composite parent, String text) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setFont(parent.getFont());
		return button;
	}
	
	/**
	 * ラジオボタンを生成し、その結果を返します。
	 * @param parent ラジオボタンを配置するコンテナ
	 * @param text 表示文字列
	 * @return 生成されたラジオボタン
	 */
	public static Button createRadio(Composite parent, String text) {
		return createRadio(parent, text, 1);
	}
	
	/**
	 * ラジオボタンを生成し、その結果を返します。
	 * @param parent ラジオボタンを配置するコンテナ
	 * @param text 表示文字列
	 * @param span このコンポーネントが占めるセル数
	 * @return 生成されたラジオボタン
	 */
	public static Button createRadio(Composite parent, String text, int span) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = span;
		button.setLayoutData(layoutData);
		button.setFont(parent.getFont());
		return button;
	}

}
