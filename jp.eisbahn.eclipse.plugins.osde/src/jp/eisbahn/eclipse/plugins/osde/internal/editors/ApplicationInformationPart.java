package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.normalize;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.Module;
import com.google.gadgets.Module.ModulePrefs;

public class ApplicationInformationPart extends AbstractFormPart {

	private Text titleText;
	private Text titleUrlText;
	private Text authorText;
	private Text authorEmailText;
	private Text screenshotText;
	private Text thumbnailText;
	private Text descriptionText;
	
	private ModulePrefsPage page;
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
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
		titleText = createText(sectionPanel, toolkit, 3, modifyListener);
		createLabel(sectionPanel, toolkit, "Title URL:");
		titleUrlText = createText(sectionPanel, toolkit, 3, modifyListener);
		createLabel(sectionPanel, toolkit, "Description:");
		descriptionText = toolkit.createText(sectionPanel, "", SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.heightHint = 50;
		layoutData.horizontalSpan = 3;
		descriptionText.setLayoutData(layoutData);
		descriptionText.addListener(SWT.Modify, modifyListener);
		createLabel(sectionPanel, toolkit, "Author:");
		authorText = createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Author Email:");
		authorEmailText = createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Screen Shot:");
		screenshotText = createText(sectionPanel, toolkit, 3, modifyListener);
		createLabel(sectionPanel, toolkit, "Thumbnail:");
		thumbnailText = createText(sectionPanel, toolkit, 3, modifyListener);
		//
		createLabel(sectionPanel, toolkit, "");
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