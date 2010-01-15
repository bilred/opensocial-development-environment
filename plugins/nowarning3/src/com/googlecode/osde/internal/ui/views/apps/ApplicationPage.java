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
package com.googlecode.osde.internal.ui.views.apps;

import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import static com.googlecode.osde.internal.utils.Gadgets.trim;

public class ApplicationPage implements IDetailsPage {

    private IManagedForm managedForm;

    private ApplicationView applicationView;
    private ApplicationImpl application;

    private Label idLabel;
    private Label titleLabel;
    private Label pathLabel;
    private Label consumerKeyLabel;
    private Label consumerSecretLabel;
    
    private TableViewer personList;

    public ApplicationPage(ApplicationView applicationView) {
        super();
        this.applicationView = applicationView;
    }

    public void createContents(Composite parent) {
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);
        FormToolkit toolkit = managedForm.getToolkit();
        // Basic
        Section basicSection = toolkit.createSection(parent,
                ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        basicSection.setText("Basic");
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        basicSection.setLayoutData(layoutData);
        Composite basicPane = toolkit.createComposite(basicSection);
        basicPane.setLayout(new GridLayout(2, false));
        basicSection.setClient(basicPane);
        //
        toolkit.createLabel(basicPane, "ID:");
        idLabel = toolkit.createLabel(basicPane, "");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        idLabel.setLayoutData(layoutData);
        //
        toolkit.createLabel(basicPane, "Title:");
        titleLabel = toolkit.createLabel(basicPane, "");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        titleLabel.setLayoutData(layoutData);
        //
        toolkit.createLabel(basicPane, "Path:");
        pathLabel = toolkit.createLabel(basicPane, "");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        pathLabel.setLayoutData(layoutData);
        //
        // hasApp
        Section hasAppSection = toolkit.createSection(parent,
                ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        hasAppSection.setText("hasApp");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        hasAppSection.setLayoutData(layoutData);
        Composite hasAppPane = toolkit.createComposite(hasAppSection);
        hasAppPane.setLayout(new GridLayout(2, false));
        hasAppSection.setClient(hasAppPane);
        //
        Table personTable = toolkit.createTable(hasAppPane,
                SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        personTable.setHeaderVisible(true);
        personTable.setLinesVisible(true);
        layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.heightHint = 100;
        personTable.setLayoutData(layoutData);
        TableColumn column = new TableColumn(personTable, SWT.LEFT, 0);
        column.setText("");
        column.setWidth(20);
        column = new TableColumn(personTable, SWT.LEFT, 1);
        column.setText("ID");
        column.setWidth(150);
        column = new TableColumn(personTable, SWT.LEFT, 2);
        column.setText("hasApp");
        column.setWidth(150);
        personList = new TableViewer(personTable);
        personList.setContentProvider(new PersonListContentProvider());
        personList.setLabelProvider(new PersonListLabelProvider());
        String[] columnProperties = {"icon", "id", "hasApp"};
        personList.setColumnProperties(columnProperties);
        String[] hasAppItems = {"true", "false"};
        CellEditor[] editors = {null, null, new ComboBoxCellEditor(personTable, hasAppItems, SWT.READ_ONLY)};
        personList.setCellEditors(editors);
        personList.setCellModifier(new PersonListCellModifier(personList, hasAppItems));
        //
        // OAuth
        Section oauthSection = toolkit.createSection(parent,
                ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        oauthSection.setText("OAuth");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        oauthSection.setLayoutData(layoutData);
        Composite oauthPane = toolkit.createComposite(oauthSection);
        oauthPane.setLayout(new GridLayout(2, false));
        oauthSection.setClient(oauthPane);
        //
        toolkit.createLabel(oauthPane, "Consumer key:");
        consumerKeyLabel = toolkit.createLabel(oauthPane, "");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        consumerKeyLabel.setLayoutData(layoutData);
        //
        toolkit.createLabel(oauthPane, "Consumer Secret:");
        consumerSecretLabel = toolkit.createLabel(oauthPane, "");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        consumerSecretLabel.setLayoutData(layoutData);
        //
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
        application = (ApplicationImpl) ((IStructuredSelection) selection).getFirstElement();
        idLabel.setText(trim(application.getId()));
        titleLabel.setText(trim(application.getTitle()));
        pathLabel.setText(trim(application.getPath()));
        consumerKeyLabel.setText(trim(application.getConsumerKey()));
        consumerSecretLabel.setText(trim(application.getConsumerSecret()));
        //
        try {
            PersonService personService = Activator.getDefault().getPersonService();
            List<Person> people = personService.getPeople(application);
            personList.setInput(people);
        } catch (ConnectionException e) {
            MessageDialog.openError(applicationView.getSite().getShell(), "Error",
                    "Shindig database not started yet.");
        }
    }

    private class PersonListCellModifier implements ICellModifier {
    
        private TableViewer viewer;
        private String[] items;
        
        PersonListCellModifier(TableViewer viewer, String[] items) {
            super();
            this.viewer = viewer;
            this.items = items;
        }
    
        public boolean canModify(Object element, String property) {
            return "hasApp".equals(property);
        }

        public Object getValue(Object element, String property) {
            Person person = (Person)element;
            Object result = null;
            if ("hasApp".equals(property)) {
                for (int i = 0; i < items.length; i++) {
                    String item = items[i];
                    if (item.equals(person.getHasApp().toString())) {
                        result = i;
                        break;
                    }
                }
            }
            return result;
        }

        public void modify(Object element, String property, Object value) {
            TableItem tableItem = (TableItem)element;
            Person person = (Person)tableItem.getData();
            if ("hasApp".equals(property)) {
                int i = (Integer)value;
                boolean newHasApp = Boolean.parseBoolean(items[i]);
                boolean prevHasApp = person.getHasApp();
                if (newHasApp != prevHasApp) {
                    try {
                        ApplicationService applicationService = Activator.getDefault().getApplicationService();
                        applicationService.updateHasApp(application, person, newHasApp);
                        person.setHasApp(newHasApp);
                    } catch (ConnectionException e) {
                        MessageDialog.openError(applicationView.getSite().getShell(), "Error",
                                "Shindig database not started yet.");
                    }
                }
            }
            viewer.update(person, null);
        }
    }

}
