package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class GadgetXmlEditor extends FormEditor {
	
	@Override
	protected void addPages() {
		try {
			ModulePrefsPage modulePrefsPage = new ModulePrefsPage(this);
			addPage(modulePrefsPage);
		} catch(PartInitException e) {
			// TODO あとで修正
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		System.out.println("editor");
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

}
