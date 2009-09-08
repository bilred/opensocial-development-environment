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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import java.util.List;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import com.google.gadgets.model.Module;
import com.google.gadgets.model.MessageBundle.Msg;
import com.google.gadgets.model.Module.ModulePrefs.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class MessageBundlePage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private LocalePage page;
	private Locale locale;
	
	private TableViewer messagesList;
	
	public MessageBundlePage(LocalePage page) {
		super();
		this.page = page;
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();

		// Message bundle section
		Section messagesSection = toolkit.createSection(parent, Section.TITLE_BAR);
		messagesSection.setText("Message Bundle");
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		messagesSection.setLayoutData(layoutData);
		
		// Message bundle pane
		Composite messagesPane = toolkit.createComposite(messagesSection);
		messagesPane.setLayout(new GridLayout(2, false));
		messagesSection.setClient(messagesPane);
		final SectionPart messagesPart = new SectionPart(messagesSection);

		// Create the table
		Table messagesTable = toolkit.createTable(messagesPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		messagesTable.setHeaderVisible(true);
		messagesTable.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		messagesTable.setLayoutData(layoutData);
		
		// Create table columns
		TableColumn column = new TableColumn(messagesTable, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(messagesTable, SWT.LEFT, 1);
		column.setText("Message Name");
		column.setWidth(130);
		column = new TableColumn(messagesTable, SWT.LEFT, 2);
		column.setText("Message Content");
		column.setWidth(150);
		column = new TableColumn(messagesTable, SWT.LEFT, 3);
		column.setText("Message Description");
		column.setWidth(150);
		
		// Table Viewer in the table
		messagesList = new TableViewer(messagesTable);
		messagesList.setContentProvider(new MessagesListContentProvider());
		messagesList.setLabelProvider(new MessagesListLabelProvider());
		
		// Button pane
		Composite buttonPane = toolkit.createComposite(messagesPane);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		
		Button deleteButton = toolkit.createButton(buttonPane, "Remove", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new RemoveButtonSelectionListener(messagesPart));
	}


	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		locale = (Locale)((IStructuredSelection)selection).getFirstElement();
		messagesList.setInput(locale);
	}
	
	private class RemoveButtonSelectionListener implements SelectionListener {
		
		private SectionPart sectionPart;
		
		public RemoveButtonSelectionListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = messagesList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				Msg msg = (Msg) structured.getFirstElement();
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting message", "Do you really want to delete this message?")) {
					
					// Delete selected message
					// TODO: ugly logic
					Locale currentLocale = (Locale)messagesList.getInput();
					Module module = page.getModule();
					for (Object element : module.getModulePrefs().getRequireOrOptionalOrPreload()) {
						if (element instanceof Locale) {
							Locale locale = (Locale)element;
							if (locale.equals(currentLocale)) {
								locale.removeMessage(msg);
							}
						}
					}
					
					messagesList.refresh();
					// managedForm.fireSelectionChanged(sectionPart, new StructuredSelection(model));
					// notify the LocalePage that Module has been changed
					((SectionPart)managedForm.getParts()[0]).markDirty();
				}
			}
		}
		
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
	
}