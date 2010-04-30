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
package com.googlecode.osde.internal.editors.pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.osde.internal.common.SectionPartFactory;
import com.googlecode.osde.internal.editors.pref.UserPrefModel.DataType;

import com.googlecode.osde.internal.gadgets.model.Module;
import com.googlecode.osde.internal.gadgets.model.Module.UserPref;
import com.googlecode.osde.internal.gadgets.model.Module.UserPref.EnumValue;

import org.apache.commons.lang.StringUtils;
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

public class UserPrefsPart extends SectionPart implements IPartSelectionListener {

    private UserPrefsPage page;

    private TableViewer userPrefsList;

    public UserPrefsPart(Composite parent, IManagedForm managedForm, UserPrefsPage page) {
        super(parent, managedForm.getToolkit(), ExpandableComposite.TITLE_BAR);
        initialize(managedForm);
        this.page = page;
        createContents(getSection(), managedForm.getToolkit());
        displayInitialValue();
    }

    private void createContents(Section section, FormToolkit toolkit) {
        section.setText("User Preferences");
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(2, false));
        // UserPrefs list
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
        column.setText("Name");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Data type");
        column.setWidth(100);
        userPrefsList = new TableViewer(table);
        userPrefsList.setContentProvider(new UserPrefsListContentProvider());
        userPrefsList.setLabelProvider(new UserPrefsListLabelProvider());
        final SectionPart part = SectionPartFactory.create(section, getManagedForm());
        userPrefsList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                getManagedForm().fireSelectionChanged(part, event.getSelection());
            }
        });
        // Buttons
        Composite buttonPane = toolkit.createComposite(composite);
        buttonPane.setLayout(new GridLayout());
        layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        buttonPane.setLayoutData(layoutData);
        Button addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalAlignment = GridData.BEGINNING;
        addButton.setLayoutData(layoutData);
        addButton.addSelectionListener(new AddButtonSelectionListener());
        Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalAlignment = GridData.BEGINNING;
        deleteButton.setLayoutData(layoutData);
        deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
        //
        section.setClient(composite);
    }

    private void displayInitialValue() {
        List<UserPrefModel> models = new ArrayList<UserPrefModel>();
        Module module = getModule();
        if (module != null) {
            List<UserPref> userPrefList = module.getUserPref();
            for (UserPref userPref : userPrefList) {
                UserPrefModel model = new UserPrefModel(userPref);
                models.add(model);
            }
        }
        userPrefsList.setInput(models);
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        if (part == this) {
            return;
        }
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        userPrefsList.refresh(((IStructuredSelection) selection).getFirstElement());
    }

    private Module getModule() {
        return page.getModule();
    }

    public void setValuesToModule() {
        Module module = getModule();
        List<UserPref> userPrefList = module.getUserPref();
        userPrefList.clear();
        List<UserPrefModel> models = getUserPrefs();
        for (UserPrefModel model : models) {
            UserPref userPref = new UserPref();
            userPref.setName(model.getName());
            String displayName = model.getDisplayName();
            if (StringUtils.isNotEmpty(displayName)) {
                userPref.setDisplayName(displayName);
            }
            String defaultValue = model.getDefaultValue();
            if (StringUtils.isNotEmpty(defaultValue)) {
                userPref.setDefaultValue(defaultValue);
            }
            userPref.setRequired(String.valueOf(model.isRequired()));
            userPref.setDatatype(model.getDataType().getDisplayName());
            List<EnumValue> enumValueList = userPref.getEnumValue();
            enumValueList.clear();
            if (model.getDataType().equals(DataType.ENUM)) {
                for (Map.Entry<String, String> entry : model.getEnumValueMap().entrySet()) {
                    EnumValue enumValue = new EnumValue();
                    enumValue.setValue(entry.getKey());
                    enumValue.setDisplayValue(entry.getValue());
                    enumValueList.add(enumValue);
                }
            } else {
                // Clear enum values in model because it be fixed.
                model.getEnumValueMap().clear();
            }
            userPrefList.add(userPref);
        }
    }

    @SuppressWarnings({"unchecked"})
    private List<UserPrefModel> getUserPrefs() {
        return (List<UserPrefModel>) userPrefsList.getInput();
    }

    private class AddButtonSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            AddUserPrefDialog dialog = new AddUserPrefDialog(page.getSite().getShell());
            if (dialog.open() == Window.OK) {
                UserPrefModel model = new UserPrefModel();
                model.setName(dialog.getName());
                model.setDisplayName(dialog.getDisplayName());
                model.setDataType(dialog.getDataType());
                List<UserPrefModel> models = getUserPrefs();
                if (!contains(models, model)) {
                    models.add(model);
                    userPrefsList.refresh();
                    markDirty();
                }
            }
        }

        private boolean contains(List<UserPrefModel> models, UserPrefModel model) {
            for (UserPrefModel userPrefModel : models) {
                if (userPrefModel.getName().equals(model.getName())) {
                    return true;
                }
            }
            return false;
        }

    }

    private class DeleteButtonSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            ISelection selection = userPrefsList.getSelection();
            if (!selection.isEmpty()) {
                IStructuredSelection structured = (IStructuredSelection) selection;
                final UserPrefModel model = (UserPrefModel) structured.getFirstElement();
                String name = model.getName();
                if (MessageDialog.openConfirm(page.getSite().getShell(),
                        "Deleting UserPref", "Do you want to delete UserPref '" + name + "'?")) {
                    List<UserPrefModel> models = getUserPrefs();
                    models.remove(model);
                    userPrefsList.refresh();
                    markDirty();
                }
            }
        }

    }

    public void changeModel() {
        displayInitialValue();
    }

}
