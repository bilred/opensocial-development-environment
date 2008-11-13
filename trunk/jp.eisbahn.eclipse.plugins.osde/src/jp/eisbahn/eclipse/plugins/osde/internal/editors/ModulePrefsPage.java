package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class ModulePrefsPage extends FormPage {
	
	public ModulePrefsPage(FormEditor editor) {
		super(editor, null, "Basic");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.addPart(new ModulePrefsPart());
		//
		displayInitialValues();
	}
	
	private void displayInitialValues() {
		IEditorInput input = getEditorInput();
		IFile file = (IFile)(input.getAdapter(IResource.class));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("page");
		// TODO Auto-generated method stub
		super.doSave(monitor);
		
	}

}
