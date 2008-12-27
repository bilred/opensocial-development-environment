package jp.eisbahn.eclipse.plugins.osde.internal.views;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.normalize;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.string;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.toInteger;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
	
	private Label idLabel;
	private Text displayNameText;
	private Text aboutMeText;
	private Text ageText;
	private DateTime birthdayText;
	private Text thumbnailUrlText;

	private TableViewer friendsList;

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
		Section basicSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		basicSection.setText("Basic");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		basicSection.setLayoutData(layoutData);
		Composite basicPane = toolkit.createComposite(basicSection);
		basicPane.setLayout(new GridLayout(4, false));
		basicSection.setClient(basicPane);
		final SectionPart basicPart = new SectionPart(basicSection);
		ValueChangeListener basicValueChangeListener = new ValueChangeListener(basicPart);
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
		displayNameText.addFocusListener(basicValueChangeListener);
		//
		toolkit.createLabel(basicPane, "About me:");
		aboutMeText = toolkit.createText(basicPane, "", SWT.MULTI);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		layoutData.heightHint = 50;
		aboutMeText.setLayoutData(layoutData);
		aboutMeText.addFocusListener(basicValueChangeListener);
		//
		toolkit.createLabel(basicPane, "Age:");
		ageText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		ageText.setLayoutData(layoutData);
		ageText.addFocusListener(basicValueChangeListener);
		//
		toolkit.createLabel(basicPane, "Birthday:");
		birthdayText = new DateTime(basicPane, SWT.DATE);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		birthdayText.setLayoutData(layoutData);
		birthdayText.addFocusListener(basicValueChangeListener);
		//
		toolkit.createLabel(basicPane, "Thumbnail:");
		thumbnailUrlText = toolkit.createText(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		thumbnailUrlText.setLayoutData(layoutData);
		thumbnailUrlText.addFocusListener(basicValueChangeListener);
		//
		// Friends
		Section friendsSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		friendsSection.setText("Relationship");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		friendsSection.setLayoutData(layoutData);
		Composite friendsPane = toolkit.createComposite(friendsSection);
		friendsPane.setLayout(new GridLayout(4, false));
		friendsSection.setClient(friendsPane);
		//
		Table friendsTable = toolkit.createTable(friendsPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		friendsTable.setHeaderVisible(true);
		friendsTable.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		friendsTable.setLayoutData(layoutData);
		TableColumn column = new TableColumn(friendsTable, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(friendsTable, SWT.LEFT, 1);
		column.setText("ID");
		column.setWidth(150);
		column = new TableColumn(friendsTable, SWT.LEFT, 2);
		column.setText("Group");
		column.setWidth(150);
		friendsList = new TableViewer(friendsTable);
		friendsList.setContentProvider(new FriendListContentProvider());
		friendsList.setLabelProvider(new FriendListLabelProvider());
		//
		Composite buttonPane = toolkit.createComposite(friendsPane);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		Button addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		addButton.addSelectionListener(new AddButtonSelectionListener());
		Button deleteButton = toolkit.createButton(buttonPane, "Remove", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new RemoveButtonSelectionListener());
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
		try {
			PersonService personService = Activator.getDefault().getPersonService();
			List<RelationshipImpl> relationshipList = personService.getRelationshipList(person);
			friendsList.setInput(relationshipList);
		} catch (ConnectionException e) {
			MessageDialog.openError(personView.getSite().getShell(), "Error", "Shindig database not started yet.");
		}
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
	
	private class AddButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent evt) {
			try {
				final PersonService personService = Activator.getDefault().getPersonService();
				List<Person> people = personService.getPeople();
				int idx = -1;
				for (int i = 0; i < people.size(); i++) {
					PersonImpl target = (PersonImpl)people.get(i);
					if (target.getObjectId() == ((PersonImpl)person).getObjectId()) {
						idx = i;
					}
				}
				if (idx != -1) {
					people.remove(idx);
				}
				AddRelationshipDialog dialog = new AddRelationshipDialog(personView.getSite().getShell(), people);
				if (dialog.open() == AddRelationshipDialog.OK) {
					final String groupId = dialog.getGroupId();
					final Person target = dialog.getTarget();
					Job job = new Job("Add relationship") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							monitor.beginTask("Creating new relationship.", 2);
							final RelationshipImpl relation = personService.createRelationship(groupId, person, target);
							personView.getSite().getShell().getDisplay().syncExec(new Runnable() {
								public void run() {
									((List<RelationshipImpl>)friendsList.getInput()).add(relation);
									friendsList.refresh();
								}
							});
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			} catch (ConnectionException e) {
				MessageDialog.openError(personView.getSite().getShell(), "Error", "Shindig database not started yet.");
			}
		}
	
	}

	private class RemoveButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = friendsList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				final RelationshipImpl relation = (RelationshipImpl)structured.getFirstElement();
				if (MessageDialog.openConfirm(personView.getSite().getShell(),
						"Deleting relationship", "Do you want to delete relationship '"
						+ relation.getPerson().getId()+ "' and '"
						+ relation.getTarget().getId()+ "'?")) {
					Job job = new Job("Deleting relationship") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							monitor.beginTask("Deleting relationship from Shindig database.", 1);
							personView.getSite().getShell().getDisplay().syncExec(new Runnable() {
								public void run() {
									try {
										PersonService personService = Activator.getDefault().getPersonService();
										personService.deleteRelationship(relation);
										List<RelationshipImpl> input = (List<RelationshipImpl>)friendsList.getInput();
										input.remove(relation);
										friendsList.refresh();
									} catch(ConnectionException e) {
										MessageDialog.openError(personView.getSite().getShell(), "Error", "Shindig database not started yet.");
									}
								}
							});
							monitor.worked(1);
							monitor.done();
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			}
		}
	
	}

}