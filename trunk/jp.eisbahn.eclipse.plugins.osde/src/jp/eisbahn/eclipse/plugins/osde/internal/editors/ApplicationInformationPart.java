package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.google.gadgets.Module;
import com.google.gadgets.Module.ModulePrefs;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.normalize;

public class ApplicationInformationPart extends AbstractFormPart {

	private Text titleText;
	private Text titleUrlText;
	private Text authorText;
	private Text authorEmailText;
	private Text screenshotText;
	private Text thumbnailText;
	private Text descriptionText;
	
	private ModulePrefsPage page;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			markDirty();
		}
	};
	
	public ApplicationInformationPart(ModulePrefsPage page) {
		this.page = page;
	}
	
	private Module getModule() {
		return page.getModule();
	}
	
	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
		createControls(form);
		displayInitialValue();
	}
	
	private void displayInitialValue() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		titleText.setText(trim(modulePrefs.getTitle()));
		titleUrlText.setText(trim(modulePrefs.getTitleUrl()));
		descriptionText.setText(trim(modulePrefs.getDescription()));
		authorText.setText(trim(modulePrefs.getAuthor()));
		authorEmailText.setText(trim(modulePrefs.getAuthorEmail()));
		screenshotText.setText(trim(modulePrefs.getScreenshot()));
		thumbnailText.setText(trim(modulePrefs.getThumbnail()));
	}

	private void createControls(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Application information");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Attributes");
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
		titleText = createText(sectionPanel, toolkit, 3);
		createLabel(sectionPanel, toolkit, "Title URL:");
		titleUrlText = createText(sectionPanel, toolkit, 3);
		createLabel(sectionPanel, toolkit, "Description:");
		descriptionText = toolkit.createText(sectionPanel, "", SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.heightHint = 50;
		layoutData.horizontalSpan = 3;
		descriptionText.setLayoutData(layoutData);
		descriptionText.addModifyListener(modifyListener);
		createLabel(sectionPanel, toolkit, "Author:");
		authorText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Author Email:");
		authorEmailText = createText(sectionPanel, toolkit);
		createLabel(sectionPanel, toolkit, "Screen Shot:");
		screenshotText = createText(sectionPanel, toolkit, 3);
		createLabel(sectionPanel, toolkit, "Thumbnail:");
		thumbnailText = createText(sectionPanel, toolkit, 3);
		
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
		return createText(parent, toolkit, 1);
	}
	
	private Text createText(Composite parent, FormToolkit toolkit, int span) {
		Text text = toolkit.createText(parent, "");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = span;
		text.setLayoutData(layoutData);
		text.addModifyListener(modifyListener);
		return text;
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		if (!onSave) {
			return;
		} else {
			setValuesToModule();
		}
	}

	private void setValuesToModule() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		modulePrefs.setTitle(normalize(titleText.getText()));
		modulePrefs.setTitleUrl(normalize(titleUrlText.getText()));
		modulePrefs.setDescription(normalize(descriptionText.getText()));
		modulePrefs.setAuthor(normalize(authorText.getText()));
		modulePrefs.setAuthorEmail(normalize(authorEmailText.getText()));
		modulePrefs.setScreenshot(normalize(screenshotText.getText()));
		modulePrefs.setThumbnail(normalize(thumbnailText.getText()));
	}

}