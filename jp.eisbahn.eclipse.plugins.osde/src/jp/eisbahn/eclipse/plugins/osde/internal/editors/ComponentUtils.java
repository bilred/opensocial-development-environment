package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ComponentUtils {

	public static Label createLabel(Composite parent, FormToolkit toolkit, String text) {
		Label label = toolkit.createLabel(parent, text);
		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.BEGINNING;
		layoutData.verticalIndent = 7;
		label.setLayoutData(layoutData);
		return label;
	}
	
	public static Text createText(Composite parent, FormToolkit toolkit, ModifyListener modifyListener) {
		return createText(parent, toolkit, 1, modifyListener);
	}
	
	public static Text createText(Composite parent, FormToolkit toolkit, int span, ModifyListener modifyListener) {
		Text text = toolkit.createText(parent, "");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = span;
		text.setLayoutData(layoutData);
		text.addModifyListener(modifyListener);
		return text;
	}
	
	public static Button createRadio(Composite parent, FormToolkit toolkit, String text) {
		return createRadio(parent, toolkit, text, 1);
	}
	
	public static Button createRadio(Composite parent, FormToolkit toolkit, String text, int span) {
		Button button = toolkit.createButton(parent, text, SWT.RADIO);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = span;
		button.setLayoutData(layoutData);
		return button;
	}

}
