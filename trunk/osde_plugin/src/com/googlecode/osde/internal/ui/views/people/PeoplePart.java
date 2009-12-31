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
package com.googlecode.osde.internal.ui.views.people;

import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class PeoplePart extends SectionPart implements IPartSelectionListener {

    private TableViewer personList;
    private PersonView personView;

    public PeoplePart(Composite parent, IManagedForm managedForm, PersonView personView) {
        super(parent, managedForm.getToolkit(), ExpandableComposite.TITLE_BAR);
        initialize(managedForm);
        createContents(getSection(), managedForm.getToolkit());
        this.personView = personView;
    }

    private void createContents(Section section, FormToolkit toolkit) {
        section.setText("People ");
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(2, false));
        // Person list
        Table table = toolkit.createTable(composite,
                SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
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
        addButton.addSelectionListener(new NewButtonSelectionListener());
        Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalAlignment = GridData.BEGINNING;
        deleteButton.setLayoutData(layoutData);
        deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
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
        personList.refresh((Person) ((IStructuredSelection) selection).getFirstElement());
    }

    private class DeleteButtonSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            ISelection selection = personList.getSelection();
            if (!selection.isEmpty()) {
                IStructuredSelection structured = (IStructuredSelection) selection;
                final Person person = (Person) structured.getFirstElement();
                if (MessageDialog.openConfirm(personView.getSite().getShell(),
                        "Deleting person",
                        "Do you want to delete person '" + person.getId() + "'?")) {
                    Job job = new Job("Create new person") {
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            monitor.beginTask("Deleting person from Shindig database.", 1);
                            personView.getSite().getShell().getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    try {
                                        PersonService personService =
                                                Activator.getDefault().getPersonService();
                                        personService.deletePerson(person);
                                        List<Person> input = (List<Person>) personList.getInput();
                                        input.remove(person);
                                        personList.refresh();
                                    } catch (ConnectionException e) {
                                        MessageDialog
                                                .openError(personView.getSite().getShell(), "Error",
                                                        "Shindig database not started yet.");
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

    private class NewButtonSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            if (Activator.getDefault().isRunningShindig()) {
                NewPersonDialog dialog = new NewPersonDialog(getSection().getShell());
                if (dialog.open() == Window.OK) {
                    final String id = dialog.getId();
                    final String displayName = dialog.getDisplayName();
                    Job job = new Job("Create new person") {
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            monitor.beginTask("Creating new person to Shindig database.", 1);
                            personView.getSite().getShell().getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    try {
                                        PersonService service =
                                                Activator.getDefault().getPersonService();
                                        Person person = service.createNewPerson(id, displayName);
                                        List<Person> input = (List<Person>) personList.getInput();
                                        input.add(person);
                                        personList.refresh();
                                    } catch (ConnectionException e) {
                                        MessageDialog
                                                .openError(personView.getSite().getShell(), "Error",
                                                        "Shindig database not started yet.");
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
