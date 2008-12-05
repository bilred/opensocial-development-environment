package jp.eisbahn.eclipse.plugins.osde.internal.views;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

class PersonPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	private Label idLabel;
	private Text displayNameText;
	private PersonView personView;
	private Person person;
	
	public PersonPage(PersonView personView) {
		super();
		this.personView = personView;
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();
		// Basic
		Section basicSection = toolkit.createSection(parent, Section.TITLE_BAR);
		basicSection.setText("Basic");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		basicSection.setLayoutData(layoutData);
		Composite basicPane = toolkit.createComposite(basicSection);
		basicPane.setLayout(new GridLayout(4, false));
		basicSection.setClient(basicPane);
		final SectionPart part = new SectionPart(basicSection);
		toolkit.createLabel(basicPane, "ID:");
		idLabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		idLabel.setLayoutData(layoutData);
		toolkit.createLabel(basicPane, "Display name:");
		displayNameText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		displayNameText.setLayoutData(layoutData);
		displayNameText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				person.setDisplayName(displayNameText.getText());
				PersonService personService = personView.getPersonService();
				personService.save(person);
				managedForm.fireSelectionChanged(part, new StructuredSelection(person));
			}
		});
	}

	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		person = (Person)((IStructuredSelection)selection).getFirstElement();
		idLabel.setText(trim(person.getId()));
		displayNameText.setText(trim(person.getDisplayName()));
	}
}