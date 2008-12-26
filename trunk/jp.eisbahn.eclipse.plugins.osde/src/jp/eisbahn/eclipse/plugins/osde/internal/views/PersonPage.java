package jp.eisbahn.eclipse.plugins.osde.internal.views;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.normalize;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.string;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.toInteger;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;

import java.util.Calendar;
import java.util.Date;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
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
	
	private PersonView personView;
	private Person person;
	
	private ValueChangeListener valueChangeListener;

	private Label idLabel;
	private Text displayNameText;
	private Text aboutMeText;
	private Text ageText;
	private DateTime birthdayText;
	private Text thumbnailUrlText;

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
		valueChangeListener = new ValueChangeListener(part);
		//
		toolkit.createLabel(basicPane, "ID:");
		idLabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		idLabel.setLayoutData(layoutData);
		//
		toolkit.createLabel(basicPane, "Display name:");
		displayNameText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		displayNameText.setLayoutData(layoutData);
		displayNameText.addFocusListener(valueChangeListener);
		//
		toolkit.createLabel(basicPane, "About me:");
		aboutMeText = toolkit.createText(basicPane, "", SWT.MULTI);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		layoutData.heightHint = 50;
		aboutMeText.setLayoutData(layoutData);
		aboutMeText.addFocusListener(valueChangeListener);
		//
		toolkit.createLabel(basicPane, "Age:");
		ageText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		ageText.setLayoutData(layoutData);
		ageText.addFocusListener(valueChangeListener);
		//
		toolkit.createLabel(basicPane, "Birthday:");
		birthdayText = new DateTime(basicPane, SWT.DATE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		birthdayText.setLayoutData(layoutData);
		birthdayText.addFocusListener(valueChangeListener);
		//
		toolkit.createLabel(basicPane, "Thumbnail:");
		thumbnailUrlText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		thumbnailUrlText.setLayoutData(layoutData);
		thumbnailUrlText.addFocusListener(valueChangeListener);
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
		aboutMeText.setText(trim(person.getAboutMe()));
		ageText.setText(string(person.getAge()));
		setDate(birthdayText, person.getBirthday());
		thumbnailUrlText.setText(trim(person.getThumbnailUrl()));
	}
	
	private void setDate(DateTime dateTime, Date date) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.setTime(date);
			dateTime.setYear(cal.get(Calendar.YEAR));
			dateTime.setMonth(cal.get(Calendar.MONTH));
			dateTime.setDay(cal.get(Calendar.DATE));
		}
	}
	
	private class ValueChangeListener implements FocusListener {
		private final SectionPart part;

		private ValueChangeListener(SectionPart part) {
			this.part = part;
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent evt) {
			try {
				person.setDisplayName(normalize(displayNameText.getText()));
				person.setAboutMe(aboutMeText.getText());
				person.setAge(toInteger(ageText.getText()));
				person.setBirthday(getDate(birthdayText));
				person.setThumbnailUrl(normalize(thumbnailUrlText.getText()));
				PersonService personService = Activator.getDefault().getPersonService();
				personService.store(person);
				managedForm.fireSelectionChanged(part, new StructuredSelection(person));
			} catch(ConnectionException e) {
				MessageDialog.openError(personView.getSite().getShell(), "Error", "Shindig database not started yet.");
			}
		}
		
		private Date getDate(DateTime dateTime) {
			int year = dateTime.getYear();
			int month = dateTime.getMonth();
			int day = dateTime.getDay();
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(year, month, day);
			return cal.getTime();
		}
	}

}