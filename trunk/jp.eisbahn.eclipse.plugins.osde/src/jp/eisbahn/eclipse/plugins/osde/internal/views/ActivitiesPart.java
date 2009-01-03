package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ActivityService;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class ActivitiesPart extends SectionPart implements IPartSelectionListener {
	
	private ActivitiesView activitiesView;
	private Combo peopleCombo;
	private TableViewer activityList;

	public ActivitiesPart(Composite parent, IManagedForm managedForm, ActivitiesView activitiesView) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		createContents(getSection(), managedForm.getToolkit());
		this.activitiesView = activitiesView;
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("Activities ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		section.setClient(composite);
		//
		toolkit.createLabel(composite, "Person:");
		peopleCombo = new Combo(composite, SWT.READ_ONLY);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		peopleCombo.setLayoutData(layoutData);
		peopleCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateActivityList();
			}
		});
		//
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Title");
		column.setWidth(160);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Application");
		column.setWidth(150);
		activityList = new TableViewer(table);
		activityList.setContentProvider(new ActivityListContentProvider());
		activityList.setLabelProvider(new ActivityListLabelProvider());
		final SectionPart part = new SectionPart(section);
		activityList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
	}

	private void updateActivityList() {
		try {
			Person person = (Person)peopleCombo.getData(peopleCombo.getItem(peopleCombo.getSelectionIndex()));
			ActivityService activityService = Activator.getDefault().getActivityService();
			List<Activity> activities = (List<Activity>)activityService.getActivities(person);
			activityList.setInput(activities);
		} catch (ConnectionException e) {
			MessageDialog.openError(activitiesView.getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}

	public void setPeople(List<Person> people) {
		peopleCombo.removeAll();
		for (Person person : people) {
			peopleCombo.add(person.getId());
			peopleCombo.setData(person.getId(), person);
		}
		peopleCombo.select(0);
		updateActivityList();
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
	}

}
