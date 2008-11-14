package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.gadgets.Module;

public class ModulePrefsPage extends FormPage {
	
	private Module module;
	
	public Module getModule() {
		return module;
	}

	public ModulePrefsPage(FormEditor editor, Module module) {
		super(editor, null, "Basic");
		this.module = module;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.addPart(new ApplicationInformationPart(this));
		managedForm.addPart(new FeaturesPart(this));
	}
	
}
