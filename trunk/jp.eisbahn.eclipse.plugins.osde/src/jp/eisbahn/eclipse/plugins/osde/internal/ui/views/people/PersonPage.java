/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.people;

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

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Person.Gender;
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
import org.eclipse.swt.widgets.Combo;
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

public class PersonPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private PersonView personView;
	private Person person;
	
	private TableViewer friendsList;

	private Label idLabel;
	private Text displayNameText;
	private Text aboutMeText;
	private Text ageText;
	private DateTime birthdayText;
	private Text thumbnailUrlText;
	private Combo genderCombo;
	private Text nicknameText;
	private Text childrenText;
	private Text ethnicityText;
	private Text fashionText;
	private Text happiestWhenText;
	private Text relationshipStatusText;
	private Text humorText;
	private Text jobInterestsText;
	private Text livingArrangementText;
	private Text petsText;

	private Text politicalViewsText;

	private Text profileUrlText;

	private Text religionText;

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
		displayNameText = toolkit.createText(basicPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		displayNameText.setLayoutData(layoutData);
		displayNameText.addFocusListener(basicValueChangeListener);
		//
		toolkit.createLabel(basicPane, "Thumbnail:");
		thumbnailUrlText = toolkit.createText(basicPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		thumbnailUrlText.setLayoutData(layoutData);
		thumbnailUrlText.addFocusListener(basicValueChangeListener);
		//
		// General
		Section generalSection = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		generalSection.setText("General");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		generalSection.setLayoutData(layoutData);
		Composite generalPane = toolkit.createComposite(generalSection);
		generalPane.setLayout(new GridLayout(2, false));
		generalSection.setClient(generalPane);
		final SectionPart generalPart = new SectionPart(generalSection);
		ValueChangeListener generalValueChangeListener = new ValueChangeListener(generalPart);
		//
		toolkit.createLabel(generalPane, "About me:");
		aboutMeText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		aboutMeText.setLayoutData(layoutData);
		aboutMeText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Age:");
		ageText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		ageText.setLayoutData(layoutData);
		ageText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Birthday:");
		birthdayText = new DateTime(generalPane, SWT.DATE | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		birthdayText.setLayoutData(layoutData);
		birthdayText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Gender:");
		genderCombo = new Combo(generalPane, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		genderCombo.setLayoutData(layoutData);
		genderCombo.addSelectionListener(generalValueChangeListener);
		genderCombo.add("");
		genderCombo.add("male");
		genderCombo.add("female");
		//
		toolkit.createLabel(generalPane, "Nickname:");
		nicknameText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		nicknameText.setLayoutData(layoutData);
		nicknameText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Relationship");
		relationshipStatusText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		relationshipStatusText.setLayoutData(layoutData);
		relationshipStatusText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Children:");
		childrenText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		childrenText.setLayoutData(layoutData);
		childrenText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Ethnicity:");
		ethnicityText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		ethnicityText.setLayoutData(layoutData);
		ethnicityText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Fashion:");
		fashionText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		fashionText.setLayoutData(layoutData);
		fashionText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Happiest when:");
		happiestWhenText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		happiestWhenText.setLayoutData(layoutData);
		happiestWhenText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Humor:");
		humorText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		humorText.setLayoutData(layoutData);
		humorText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Job interests:");
		jobInterestsText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		jobInterestsText.setLayoutData(layoutData);
		jobInterestsText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Living arrangement:");
		livingArrangementText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		livingArrangementText.setLayoutData(layoutData);
		livingArrangementText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Pets:");
		petsText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		petsText.setLayoutData(layoutData);
		petsText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Political views:");
		politicalViewsText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		politicalViewsText.setLayoutData(layoutData);
		politicalViewsText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Profile URL:");
		profileUrlText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		profileUrlText.setLayoutData(layoutData);
		profileUrlText.addFocusListener(generalValueChangeListener);
		//
		toolkit.createLabel(generalPane, "Religion:");
		religionText = toolkit.createText(generalPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		religionText.setLayoutData(layoutData);
		religionText.addFocusListener(generalValueChangeListener);
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
		genderCombo.select(0);
		Gender gender = person.getGender();
		if (gender != null) {
			String[] items = genderCombo.getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(gender.name())) {
					genderCombo.select(i);
					break;
				}
			}
		}
		nicknameText.setText(trim(person.getNickname()));
		relationshipStatusText.setText(trim(person.getRelationshipStatus()));
		childrenText.setText(trim(person.getChildren()));
		ethnicityText.setText(trim(person.getEthnicity()));
		fashionText.setText(trim(person.getFashion()));
		happiestWhenText.setText(trim(person.getHappiestWhen()));
		humorText.setText(trim(person.getHumor()));
		jobInterestsText.setText(trim(person.getJobInterests()));
		livingArrangementText.setText(trim(person.getLivingArrangement()));
		petsText.setText(trim(person.getPets()));
		politicalViewsText.setText(trim(person.getPoliticalViews()));
		profileUrlText.setText(trim(person.getProfileUrl()));
		religionText.setText(trim(person.getReligion()));
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
	
	private class ValueChangeListener implements FocusListener, SelectionListener {
		private final SectionPart part;

		private ValueChangeListener(SectionPart part) {
			this.part = part;
		}

		public void widgetSelected(SelectionEvent e) {
			updatePerson();
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent evt) {
			updatePerson();
		}
		
		private void updatePerson() {
			try {
				person.setDisplayName(normalize(displayNameText.getText()));
				person.setAboutMe(normalize(aboutMeText.getText()));
				person.setAge(toInteger(ageText.getText()));
				person.setBirthday(getDate(birthdayText));
				person.setThumbnailUrl(normalize(thumbnailUrlText.getText()));
				String gender = genderCombo.getText();
				person.setGender(StringUtils.isNotEmpty(gender) ? Gender.valueOf(gender) : null);
				person.setNickname(normalize(nicknameText.getText()));
				person.setRelationshipStatus(normalize(relationshipStatusText.getText()));
				person.setChildren(normalize(childrenText.getText()));
				person.setEthnicity(normalize(ethnicityText.getText()));
				person.setFashion(normalize(fashionText.getText()));
				person.setHappiestWhen(normalize(happiestWhenText.getText()));
				person.setHumor(normalize(humorText.getText()));
				person.setJobInterests(normalize(jobInterestsText.getText()));
				person.setLivingArrangement(normalize(livingArrangementText.getText()));
				person.setPets(normalize(petsText.getText()));
				person.setPoliticalViews(normalize(politicalViewsText.getText()));
				person.setProfileUrl(normalize(profileUrlText.getText()));
				person.setReligion(normalize(religionText.getText()));
				//
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

		public void widgetDefaultSelected(SelectionEvent e) {
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