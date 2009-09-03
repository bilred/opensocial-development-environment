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

import com.google.gadgets.Module.ModulePrefs.Locale;

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
		column.setText("Name");
		column.setWidth(130);
		column = new TableColumn(messagesTable, SWT.LEFT, 2);
		column.setText("Text");
		column.setWidth(150);
		
		// Table Viewer in the table
		messagesList = new TableViewer(messagesTable);
		messagesList.setContentProvider(new ArrayContentProvider());
		messagesList.setLabelProvider(new MessagesListLabelProvider());
		
		// Button pane
		Composite buttonPane = toolkit.createComposite(messagesPane);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		Button addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		addButton.addSelectionListener(new AddButtonSelectionListener(messagesPart));
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
		messagesList.setInput(locale.getMessageBundle().getMessages());
	}

	private void makeDirty() {
		page.updateLocaleModel();
	}

	private class AddButtonSelectionListener implements SelectionListener {
		
		private SectionPart sectionPart;
		
		public AddButtonSelectionListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		// TODO: Add button for new messages given the selected locale
		public void widgetSelected(SelectionEvent e) {
			/*
			List<LocaleModel> localeModels = page.getLocaleModels();
			AddMessageDialog dialog = new AddMessageDialog(page.getSite().getShell(), localeModels, model);
			if (dialog.open() == AddMessageDialog.OK) {
				String name = dialog.getName();
				Map<LocaleModel, String> contentMap = dialog.getContentMap();
				for (LocaleModel localeModel : localeModels) {
					String content = contentMap.get(localeModel);
					if (!StringUtils.isEmpty(content)) {
						localeModel.getMessages().put(name, content);
					} else {
						localeModel.getMessages().remove(name);
					}
				}
				managedForm.fireSelectionChanged(sectionPart, new StructuredSelection(model));
				makeDirty();
			}
			*/
		}
		
	}
	
	private class RemoveButtonSelectionListener implements SelectionListener {
		
		private SectionPart sectionPart;
		
		public RemoveButtonSelectionListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			/*
			ISelection selection = messagesList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				final Map.Entry<String, String> message = (Map.Entry<String, String>)structured.getFirstElement();
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting message", "Do you want to delete message '" + message.getKey() + "'?")) {
					List<LocaleModel> localeModels = page.getLocaleModels();
					for (LocaleModel localeModel : localeModels) {
						localeModel.getMessages().remove(message.getKey());
					}
					messagesList.refresh();
					managedForm.fireSelectionChanged(sectionPart, new StructuredSelection(model));
					makeDirty();
				}
			}
			*/
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