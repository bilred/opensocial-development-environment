package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class ModulePrefsPage extends FormPage {

	public ModulePrefsPage(FormEditor editor) {
		super(editor, null, "Basic");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Basic information");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Application information");
		section.setDescription("These fields describe this OpenSocial application.");
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Composite sectionPanel = toolkit.createComposite(section);
		sectionPanel.setLayout(new GridLayout());
		section.setClient(sectionPanel);
		//
		
	}

}
