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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.pref;

import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefModel.DataType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.*;

public class UserPrefPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private UserPrefsPage page;
	private UserPrefModel model;
	
	private Text nameText;

	private Label errorLabel;
	
	private boolean initialize = false;

	private Text displayNameText;

	private Text defaultValueText;

	private Button requiredButton;

	private Combo dataTypeCombo;

	private Section enumValuesSection;

	private TableViewer enumValuesList;
	
	public UserPrefPage(UserPrefsPage page) {
		super();
		this.page = page;
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section detailsSection = toolkit.createSection(parent, Section.TITLE_BAR);
		detailsSection.setText("Details");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		detailsSection.setLayoutData(layoutData);
		Composite detailsPane = toolkit.createComposite(detailsSection);
		detailsPane.setLayout(new GridLayout(2, false));
		detailsSection.setClient(detailsPane);
		final SectionPart detailsPart = new SectionPart(detailsSection);
		UpdateFieldsListener listener = new UpdateFieldsListener(detailsPart);
		//
		toolkit.createLabel(detailsPane, "Name:");
		nameText = toolkit.createText(detailsPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(layoutData);
		nameText.addModifyListener(listener);
		//
		toolkit.createLabel(detailsPane, "Display name:");
		displayNameText = toolkit.createText(detailsPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		displayNameText.setLayoutData(layoutData);
		displayNameText.addModifyListener(listener);
		//
		toolkit.createLabel(detailsPane, "Default value:");
		defaultValueText = toolkit.createText(detailsPane, "", SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		defaultValueText.setLayoutData(layoutData);
		defaultValueText.addModifyListener(listener);
		//
		requiredButton = toolkit.createButton(detailsPane, "Required", SWT.CHECK);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		requiredButton.setLayoutData(layoutData);
		requiredButton.addSelectionListener(listener);
		//
		toolkit.createLabel(detailsPane, "Data type:");
		dataTypeCombo = new Combo(detailsPane, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		dataTypeCombo.setLayoutData(layoutData);
		DataType[] values = DataType.values();
		for (DataType dataType : values) {
			dataTypeCombo.add(dataType.getDisplayName());
			dataTypeCombo.setData(dataType.getDisplayName(), dataType);
		}
		dataTypeCombo.addSelectionListener(listener);
		//
		errorLabel = toolkit.createLabel(detailsPane, "");
		errorLabel.setForeground(Activator.getDefault().getColor(new RGB(255, 0, 0)));
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		errorLabel.setLayoutData(layoutData);
		//
		// Enum values
		enumValuesSection = toolkit.createSection(parent, Section.TITLE_BAR);
		enumValuesSection.setText("Enum values");
		layoutData = new GridData(GridData.FILL_BOTH);
		enumValuesSection.setLayoutData(layoutData);
		Composite enumValuesPane = toolkit.createComposite(enumValuesSection);
		enumValuesPane.setLayout(new GridLayout(2, false));
		enumValuesSection.setClient(enumValuesPane);
		final SectionPart enumValuesPart = new SectionPart(enumValuesSection);
		//
		Table table = toolkit.createTable(enumValuesPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Value");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Display value");
		column.setWidth(150);
		enumValuesList = new TableViewer(table);
		enumValuesList.setContentProvider(new EnumValuesListContentProvider());
		enumValuesList.setLabelProvider(new EnumValuesListLabelProvider());
		// Buttons
		Composite buttonPane = toolkit.createComposite(enumValuesPane);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		Button addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		addButton.addSelectionListener(new AddButtonSelectionListener(enumValuesPart));
		Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new DeleteButtonSelectionListener(enumValuesPart));
	}

	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		initialize = false;
		model = (UserPrefModel)((IStructuredSelection)selection).getFirstElement();
		nameText.setText(model.getName());
		displayNameText.setText(trim(model.getDisplayName()));
		defaultValueText.setText(trim(model.getDefaultValue()));
		requiredButton.setSelection(model.isRequired());
		DataType dataType = model.getDataType();
		String[] items = dataTypeCombo.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(dataType.getDisplayName())) {
				dataTypeCombo.select(i);
				break;
			}
		}
		enumValuesSection.setVisible(dataType.equals(DataType.ENUM));
		enumValuesList.setInput(model.getEnumValueMap());
		errorLabel.setText("");
		initialize = true;
	}
	
	private void makeDirty() {
		page.updateUserPrefModel();
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
	
	private class UpdateFieldsListener implements ModifyListener, SelectionListener {
		
		private SectionPart sectionPart;
		
		public UpdateFieldsListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void modifyText(ModifyEvent e) {
			updateFields();
		}

		public void widgetSelected(SelectionEvent e) {
			updateFields();
		}
		
		private void updateFields() {
			if (initialize) {
				String name = nameText.getText();
				if (StringUtils.isEmpty(name)) {
					errorLabel.setText("name is empty.");
					return;
				}
				model.setName(name);
				model.setDisplayName(normalize(displayNameText.getText()));
				model.setDefaultValue((normalize(defaultValueText.getText())));
				model.setRequired(requiredButton.getSelection());
				DataType dataType = (DataType)dataTypeCombo.getData(dataTypeCombo.getText());
				model.setDataType(dataType);
				enumValuesSection.setVisible(dataType.equals(DataType.ENUM));
				errorLabel.setText("");
				managedForm.fireSelectionChanged(sectionPart, new StructuredSelection(model));
				makeDirty();
			}
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {
		}

	}
	
	private class AddButtonSelectionListener implements SelectionListener {
		
		private SectionPart sectionPart;
		
		public AddButtonSelectionListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			AddEnumValueDialog dialog = new AddEnumValueDialog(page.getSite().getShell());
			if (dialog.open() == AddEnumValueDialog.OK) {
				Map<String, String> enumValuesMap = (Map<String, String>)enumValuesList.getInput();
				enumValuesMap.put(dialog.getValue(), dialog.getDisplayValue());
				enumValuesList.refresh();
				managedForm.fireSelectionChanged(sectionPart, new StructuredSelection(model));
				makeDirty();
			}
		}
		
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {

		private SectionPart sectionPart;
		
		public DeleteButtonSelectionListener(SectionPart sectionPart) {
			this.sectionPart = sectionPart;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = enumValuesList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				Map.Entry<String, String> model = (Map.Entry<String, String>)structured.getFirstElement();
				String value = model.getKey();
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting Enum value", "Do you want to delete Enum value '" + value + "'?")) {
					Map<String, String> enumValuesMap = (Map<String, String>)enumValuesList.getInput();
					enumValuesMap.remove(model.getKey());
					enumValuesList.refresh();
					makeDirty();
				}
			}
		}
		
	}
	
}