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

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
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

public class ApplicationsPart extends SectionPart implements IPartSelectionListener {

    private TableViewer applicationList;
    private ApplicationView applicationView;

    public ApplicationsPart(Composite parent, IManagedForm managedForm, ApplicationView applicationView) {
        super(parent, managedForm.getToolkit(), ExpandableComposite.TITLE_BAR);
        initialize(managedForm);
        createContents(getSection(), managedForm.getToolkit());
        this.applicationView = applicationView;
    }

    private void createContents(Section section, FormToolkit toolkit) {
        section.setText("Application ");
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(2, false));
        // Application list
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
        column.setText("Title");
        column.setWidth(120);
        applicationList = new TableViewer(table);
        applicationList.setContentProvider(new ApplicationListContentProvider());
        applicationList.setLabelProvider(new ApplicationListLabelProvider());
        final SectionPart part = new SectionPart(section);
        applicationList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                getManagedForm().fireSelectionChanged(part, event.getSelection());
            }
        });
        // Buttons
        Composite buttonPane = toolkit.createComposite(composite);
        buttonPane.setLayout(new GridLayout());
        layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        buttonPane.setLayoutData(layoutData);
        Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalAlignment = GridData.BEGINNING;
        deleteButton.setLayoutData(layoutData);
        deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
        //
        section.setClient(composite);
    }

    public void setApplications(List<ApplicationImpl> applications) {
        applicationList.setInput(applications);
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        if (part == this) {
            return;
        }
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        applicationList.refresh(((IStructuredSelection) selection).getFirstElement());
    }

    private class DeleteButtonSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            ISelection selection = applicationList.getSelection();
            if (!selection.isEmpty()) {
                IStructuredSelection structured = (IStructuredSelection) selection;
                final ApplicationImpl application = (ApplicationImpl) structured.getFirstElement();
                if (MessageDialog.openConfirm(applicationView.getSite().getShell(),
                        "Deleting application",
                        "Do you want to delete application '" + application.getTitle() + "'?")) {
                    Job job = new Job("Delete application") {
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            monitor.beginTask("Deleting application from Shindig database.", 1);
                            applicationView.getSite().getShell().getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    try {
                                        ApplicationService applicationService =
                                                Activator.getDefault().getApplicationService();
                                        applicationService.deleteApplication(application);

                                        @SuppressWarnings("unchecked")
                                        List<ApplicationImpl> input = (List<ApplicationImpl>) applicationList.getInput();
                                        input.remove(application);
                                        applicationList.refresh();
                                    } catch (ConnectionException e) {
                                        MessageDialog
                                                .openError(applicationView.getSite().getShell(), "Error",
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
