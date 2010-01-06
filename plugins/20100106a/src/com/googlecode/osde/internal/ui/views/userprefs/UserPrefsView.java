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
package com.googlecode.osde.internal.ui.views.userprefs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.editors.pref.UserPrefModel;
import com.googlecode.osde.internal.editors.pref.UserPrefModel.DataType;
import com.googlecode.osde.internal.runtime.LaunchApplicationInformation;
import com.googlecode.osde.internal.runtime.LaunchApplicationJob;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.ui.views.AbstractView;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.hibernate.entities.UserPrefImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class UserPrefsView extends AbstractView {
    private static final Logger logger = new Logger(UserPrefsView.class);
    public static final String ID = "com.googlecode.osde.internal.views.UserPrefsView";

    private Composite userPrefFieldsComposite;

    private ScrolledForm form;

    private SelectionListener listener = new SaveButtonSelectionListener();

    private Map<String, Control> fieldMap;

    private LaunchApplicationInformation information;

    public UserPrefsView() {
        super();
        this.fieldMap = new HashMap<String, Control>();
    }

    protected void createForm(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        Composite body = form.getBody();
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        body.setLayout(layout);
        Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        section.setLayoutData(layoutData);
        section.setText("Settings");
        //
        userPrefFieldsComposite = toolkit.createComposite(section);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        userPrefFieldsComposite.setLayoutData(layoutData);
        layout = new GridLayout();
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        layout.numColumns = 2;
        userPrefFieldsComposite.setLayout(layout);
        section.setClient(userPrefFieldsComposite);
    }

    public void setFocus() {
    }

    private void removeAllFields() {
        getSite().getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                Control[] children = userPrefFieldsComposite.getChildren();
                for (Control control : children) {
                    control.dispose();
                }
                fieldMap.clear();
            }
        });
    }

    private void setupFields(
            final List<UserPrefModel> userPrefModels,
            final LaunchApplicationInformation information) {
        getSite().getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                FormToolkit toolkit = new FormToolkit(getSite().getShell().getDisplay());
                for (UserPrefModel model : userPrefModels) {
                    toolkit.createLabel(userPrefFieldsComposite, model.getDisplayName());
                    DataType dataType = model.getDataType();
                    Control control = null;
                    GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
                    if (dataType.equals(DataType.STRING)) {
                        control = toolkit.createText(
                                userPrefFieldsComposite, model.getDefaultValue(), SWT.BORDER);
                    } else if (dataType.equals(DataType.BOOL)) {
                        control = new Button(userPrefFieldsComposite, SWT.CHECK);
                        control.setBackground(toolkit.getColors().getBackground());
                    } else if (dataType.equals(DataType.ENUM)) {
                        control = new Combo(userPrefFieldsComposite, SWT.READ_ONLY);
                        Map<String, String> enumValueMap = model.getEnumValueMap();
                        for (Map.Entry<String, String> entry : enumValueMap.entrySet()) {
                            String value = entry.getKey();
                            String displayValue = StringUtils.isEmpty(entry.getValue()) ? value
                                    : entry.getValue();
                            ((Combo) control).add(displayValue);
                            ((Combo) control).setData(displayValue, entry.getKey());
                        }
                        ((Combo) control).select(0);
                    } else if (dataType.equals(DataType.LIST)) {
                        control = toolkit.createText(
                                userPrefFieldsComposite, model.getDefaultValue(),
                                SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
                        layoutData.heightHint = 50;
                    } else {
                        control = toolkit.createLabel(userPrefFieldsComposite, "---");
                    }
                    control.setLayoutData(layoutData);
                    fieldMap.put(model.getName(), control);
                }
                if (!userPrefModels.isEmpty()) {
                    Button saveButton =
                            toolkit.createButton(userPrefFieldsComposite, "Save", SWT.PUSH);
                    saveButton.addSelectionListener(listener);
                    GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_END);
                    layoutData.horizontalSpan = 2;
                    saveButton.setLayoutData(layoutData);
                    Label separator =
                            toolkit.createSeparator(userPrefFieldsComposite, SWT.HORIZONTAL);
                    layoutData = new GridData(GridData.FILL_HORIZONTAL);
                    layoutData.horizontalSpan = 2;
                    separator.setLayoutData(layoutData);
                }
                createLabel(information.getUrl(), 2, toolkit);
                createLabel("Viewer:", 1, toolkit);
                createLabel(information.getViewer(), 1, toolkit);
                createLabel("Owner:", 1, toolkit);
                createLabel(information.getOwner(), 1, toolkit);
                createLabel("Locale:", 1, toolkit);
                createLabel(information.getLanguage() + "_" + information.getCountry(), 1, toolkit);
                userPrefFieldsComposite.layout();
                form.reflow(true);
            }

            private void createLabel(String text, int span, FormToolkit toolkit) {
                Label label = toolkit.createLabel(userPrefFieldsComposite, text);
                GridData layoutData = new GridData();
                layoutData.horizontalSpan = span;
                label.setLayoutData(layoutData);
            }
        });
    }

    public void connectedDatabase() {
    }

    private void retrieveUserPrefValues(
            final String appId, final String viewerId, final Map<String, UserPrefModel> models) {
        getSite().getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                try {
                    ApplicationService service = Activator.getDefault().getApplicationService();
                    List<UserPrefImpl> userPrefs = service.getUserPrefs(appId, viewerId);
                    for (UserPrefImpl userPref : userPrefs) {
                        String name = userPref.getName();
                        Control control = fieldMap.get(name);
                        UserPrefModel model = models.get(name);
                        if (model != null) {
                            DataType dataType = model.getDataType();
                            if (dataType.equals(DataType.STRING)) {
                                ((Text) control).setText(userPref.getValue());
                            } else if (dataType.equals(DataType.BOOL)) {
                                boolean value = Boolean.parseBoolean(userPref.getValue());
                                ((Button) control).setSelection(value);
                            } else if (dataType.equals(DataType.LIST)) {
                                ((Text) control).setText(userPref.getValue());
                            } else if (dataType.equals(DataType.ENUM)) {
                                Map<String, String> enumValueMap = model.getEnumValueMap();
                                String displayValue = enumValueMap.get(userPref.getValue());
                                if (StringUtils.isEmpty(displayValue)) {
                                    displayValue = userPref.getValue();
                                }
                                Combo combo = (Combo) control;
                                String[] items = combo.getItems();
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].equals(displayValue)) {
                                        combo.select(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (ConnectionException e) {
                    MessageDialog.openError(getSite().getShell(), "Error",
                            "Shindig database not started yet.");
                }
            }
        });
    }

    public void showUserPrefFields(final LaunchApplicationInformation information,
            final String url) {
        Job job = new Job("Fetch metadata") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                monitor.beginTask("Fetch metadata", 4);
                try {
                    removeAllFields();
                    monitor.worked(1);
                    List<UserPrefModel> userPrefModels =
                            MetaDataFetcher.fetch(information.getView(), information.getViewer(),
                                    information.getOwner(), information.getAppId(),
                                    information.getCountry(), information.getLanguage(), url);
                    monitor.worked(1);
                    setupFields(userPrefModels, information);
                    monitor.worked(1);
                    retrieveUserPrefValues(information.getAppId(), information.getViewer(),
                            convertMap(userPrefModels));
                    monitor.worked(1);
                    UserPrefsView.this.information = information;
                } catch (final Exception e) {
                    logger.error("To show the UserPrefs values failed.", e);
                    getSite().getShell().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            MessageDialog.openError(getSite().getShell(), "Error",
                                    "Fetching metadata failed.\n" + e.getMessage());
                        }
                    });
                }
                monitor.done();
                return Status.OK_STATUS;
            }

            private Map<String, UserPrefModel> convertMap(List<UserPrefModel> userPrefModels) {
                Map<String, UserPrefModel> resultMap = new HashMap<String, UserPrefModel>();
                for (UserPrefModel model : userPrefModels) {
                    resultMap.put(model.getName(), model);
                }
                return resultMap;
            }
        };
        job.schedule(1000);
    }

    private class SaveButtonSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent evt) {
            String appId = information.getAppId();
            String viewerId = information.getViewer();
            Map<String, String> userPrefMap = new HashMap<String, String>();
            for (Map.Entry<String, Control> entry : fieldMap.entrySet()) {
                String name = entry.getKey();
                String value = null;
                Control control = entry.getValue();
                if (control instanceof Text) {
                    value = ((Text) control).getText();
                } else if (control instanceof Combo) {
                    Combo combo = (Combo) control;
                    value = (String) combo.getData(combo.getText());
                } else if (control instanceof Button) {
                    value = Boolean.toString(((Button) control).getSelection());
                }
                if (value != null) {
                    userPrefMap.put(name, value);
                }
            }
            try {
                ApplicationService service = Activator.getDefault().getApplicationService();
                service.storeUserPrefs(appId, viewerId, userPrefMap);
                Job job = new LaunchApplicationJob("Running application", information,
                        getSite().getShell());
                job.schedule();
            } catch (ConnectionException e) {
                MessageDialog.openError(getSite().getShell(), "Error",
                        "Shindig database not started yet.");
            }
        }
    }

    public void disconnectedDatabase() {
        removeAllFields();
    }

}