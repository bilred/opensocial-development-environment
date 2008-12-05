package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class PeoplePart extends SectionPart implements IPartSelectionListener {
	
	private TableViewer personList;

	public PeoplePart(Composite parent, IManagedForm managedForm) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		createContents(getSection(), managedForm.getToolkit());
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("People ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		// Person list
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("ID");
		column.setWidth(120);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Display name");
		column.setWidth(150);
		personList = new TableViewer(table);
		personList.setContentProvider(new PersonListContentProvider());
		personList.setLabelProvider(new PersonListLabelProvider());
		final SectionPart part = new SectionPart(section);
		personList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
		// Buttons
		Composite buttonPane = toolkit.createComposite(composite);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		Button addButton = toolkit.createButton(buttonPane, "New", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		//
		section.setClient(composite);
	}

	public void setPeople(List<Person> people) {
		personList.setInput(people);
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		personList.refresh((Person)((IStructuredSelection)selection).getFirstElement());
	}

}
