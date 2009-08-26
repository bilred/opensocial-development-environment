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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.basic;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.FeatureName;
import com.google.gadgets.Module;
import com.google.gadgets.Module.ModulePrefs;
import com.google.gadgets.Module.ModulePrefs.Require;

public class FeaturesPart extends AbstractFormPart {

	private ModulePrefsPage page;
	
	private Map<FeatureName, Button> buttonMap;
	
	private TableViewer freeFraturesList;
	
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			markDirty();
		}
	};
	
	public FeaturesPart(ModulePrefsPage page) {
		this.page = page;
		buttonMap = new EnumMap<FeatureName, Button>(FeatureName.class);
	}
	
	private Module getModule() {
		return page.getModule();
	}
	
	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
		createControls(form);
		displayInitialValue();
	}
	
	private void displayInitialValue() {
		Collection<Button> buttons = buttonMap.values();
		for (Button button : buttons) {
			button.setSelection(false);
		}
		Set<String> freeFeatures = new TreeSet<String>();
		Module module = getModule();
		if (module != null) {
			ModulePrefs modulePrefs = module.getModulePrefs();
			List<Object> elements = modulePrefs.getRequireOrOptionalOrPreload();
			for (Object value : elements) {
				if (value instanceof Require) {
					Require require = (Require)value;
					String featureRealName = require.getFeature();
					FeatureName feature = FeatureName.getFeatureName(featureRealName);
					Button button = buttonMap.get(feature);
					if (button != null) {
						button.setSelection(true);
					} else {
						freeFeatures.add(featureRealName);
					}
				}
			}
		}
		freeFraturesList.setInput(freeFeatures);
	}

	private void createControls(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE);
		section.setText("Features");
		section.setDescription("The checked features will be used in your application.");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite sectionPanel = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		sectionPanel.setLayout(layout);
		section.setClient(sectionPanel);
		sectionPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite fixedFeaturesPanel = toolkit.createComposite(sectionPanel);
		layout = new GridLayout();
		layout.numColumns = 2;
		fixedFeaturesPanel.setLayout(layout);
		fixedFeaturesPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button opensocial08Button = createCheckbox(fixedFeaturesPanel, "OpenSocial v0.8", toolkit);
		buttonMap.put(FeatureName.OPENSOCIAL_0_8, opensocial08Button);
		Button opensocial07Button = createCheckbox(fixedFeaturesPanel, "OpenSocial v0.7", toolkit);
		buttonMap.put(FeatureName.OPENSOCIAL_0_7, opensocial07Button);
		Button pubsubButton = createCheckbox(fixedFeaturesPanel, "PubSub", toolkit);
		buttonMap.put(FeatureName.PUBSUB, pubsubButton);
		Button viewsButton = createCheckbox(fixedFeaturesPanel, "Views", toolkit);
		buttonMap.put(FeatureName.VIEWS, viewsButton);
		Button flashButton = createCheckbox(fixedFeaturesPanel, "Flash", toolkit);
		buttonMap.put(FeatureName.FLASH, flashButton);
		Button skinsButton = createCheckbox(fixedFeaturesPanel, "Skins", toolkit);
		buttonMap.put(FeatureName.SKINS, skinsButton);
		Button dynamicHeightButton = createCheckbox(fixedFeaturesPanel, "Dynamic Height", toolkit);
		buttonMap.put(FeatureName.DYNAMIC_HEIGHT, dynamicHeightButton);
		Button setTitleButton = createCheckbox(fixedFeaturesPanel, "Set Title", toolkit);
		buttonMap.put(FeatureName.SET_TITLE, setTitleButton);
		Button miniMessageButton = createCheckbox(fixedFeaturesPanel, "Mini Message", toolkit);
		buttonMap.put(FeatureName.MINI_MESSAGE, miniMessageButton);
		Button tabsButton = createCheckbox(fixedFeaturesPanel, "Tabs", toolkit);
		buttonMap.put(FeatureName.TABS, tabsButton);
		//
		Composite freeFeaturesPanel = toolkit.createComposite(sectionPanel);
		layout = new GridLayout();
		layout.numColumns = 2;
		freeFeaturesPanel.setLayout(layout);
		freeFeaturesPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Composite freeFeaturesTablePanel = toolkit.createComposite(freeFeaturesPanel);
		freeFeaturesTablePanel.setLayout(layout);
		freeFeaturesTablePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Table table = toolkit.createTable(freeFeaturesTablePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Name");
		column.setWidth(200);
		freeFraturesList = new TableViewer(table);
		freeFraturesList.setContentProvider(new FreeFeaturesListContentProvider());
		freeFraturesList.setLabelProvider(new FreeFeaturesListLabelProvider());
		final SectionPart part = new SectionPart(section);
		freeFraturesList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
		//
		Composite buttonPane = toolkit.createComposite(freeFeaturesPanel);
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
	}
	
	private Button createCheckbox(Composite parent, String text, FormToolkit toolkit) {
		Button button = toolkit.createButton(parent, text, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setFont(parent.getFont());
		button.addSelectionListener(selectionListener);
		return button;
	}
	
	@SuppressWarnings("unchecked")
	public void setValuesToModule() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		clearFeatures(modulePrefs);
		List<Object> requireOrOptionalOrPreload = modulePrefs.getRequireOrOptionalOrPreload();
		Set<Entry<FeatureName,Button>> set = buttonMap.entrySet();
		for (Entry<FeatureName, Button> entry : set) {
			FeatureName featureName = entry.getKey();
			Button button = entry.getValue();
			if (button.getSelection()) {
				Require require = new Require();
				require.setFeature(featureName.toString());
				requireOrOptionalOrPreload.add(require);
			}
		}
		Set<String> freeFeatures = (Set<String>)freeFraturesList.getInput();
		for (String featureName : freeFeatures) {
			Require require = new Require();
			require.setFeature(featureName);
			requireOrOptionalOrPreload.add(require);
		}
	}
	
	private void clearFeatures(ModulePrefs modulePrefs) {
		List<Object> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (int i = elements.size() - 1; i >= 0; i--) {
			Object element = elements.get(i);
			if (element instanceof Require) {
				elements.remove(i);
			}
		}
	}

	public void changeModel() {
		displayInitialValue();
	}
	
	private class AddButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent e) {
			InputDialog dialog = new InputDialog(
					page.getSite().getShell(), "Add feature", "Please input the feature name.", "",
					new IInputValidator() {
						public String isValid(String newText) {
							if (StringUtils.isBlank(newText)) {
								return "The feature name is empty.";
							} else {
								return null;
							}
						}
					}
			);
			if (dialog.open() == Dialog.OK) {
				String featureRealName = dialog.getValue();
				FeatureName featureName = FeatureName.getFeatureName(featureRealName);
				if (featureName != null) {
					buttonMap.get(featureName).setSelection(true);
				} else {
					((Set<String>)freeFraturesList.getInput()).add(featureRealName);
					freeFraturesList.refresh();
				}
				markDirty();
			}
		}
		
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@SuppressWarnings("unchecked")
		public void widgetSelected(SelectionEvent e) {
			ISelection selection = freeFraturesList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				final String feature = (String)structured.getFirstElement();
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting feature", "Do you want to delete the feature '" + feature + "'?")) {
					Set<String> models = (Set<String>)freeFraturesList.getInput();
					models.remove(feature);
					freeFraturesList.refresh();
					markDirty();
				}
			}
		}
		
	}

}