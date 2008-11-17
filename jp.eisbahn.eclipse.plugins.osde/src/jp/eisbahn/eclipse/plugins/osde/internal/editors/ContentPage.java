package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.gadgets.Module;
import com.google.gadgets.ViewName;

public class ContentPage extends FormPage {
	
	private Module module;
	private ViewName viewName;
	
	public Module getModule() {
		return module;
	}
	
	public ViewName getViewName() {
		return viewName;
	}

	public ContentPage(FormEditor editor, Module module, ViewName viewName) {
		super(editor, null, viewName.getDisplayName());
		this.module = module;
		this.viewName = viewName;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.addPart(new ContentHtmlPart(this));
	}
	
}
