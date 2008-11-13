package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class ModulePrefsPart extends AbstractFormPart {

	private Text titleText;
	private Text titleUrlText;
	private Text authorText;
	private Text authorEmailText;
	private Text screenshotText;
	private Text thumbnailText;
	
	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
		//
		createControls(form);
	}
	
	private void createControls(IManagedForm managedForm) {
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
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite sectionPanel = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.numColumns = 4;
		sectionPanel.setLayout(layout);
		section.setClient(sectionPanel);
		sectionPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		createLabel(sectionPanel, toolkit, "Title:");
		titleText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Title URL:");
		titleUrlText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Description:");
		Text descriptionText = toolkit.createText(sectionPanel, "", SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.heightHint = 50;
		layoutData.horizontalSpan = 3;
		descriptionText.setLayoutData(layoutData);
		createLabel(sectionPanel, toolkit, "Author:");
		authorText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Author Email:");
		authorEmailText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Screen Shot:");
		screenshotText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Thumbnail:");
		thumbnailText = createText(sectionPanel, toolkit);
		
	}
	
	private Label createLabel(Composite parent, FormToolkit toolkit, String text) {
		Label label = toolkit.createLabel(parent, text);
		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.BEGINNING;
		layoutData.verticalIndent = 7;
		label.setLayoutData(layoutData);
		return label;
	}
	
	private Text createText(Composite parent, FormToolkit toolkit) {
		Text text = toolkit.createText(parent, "");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

}