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
package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.FeatureName;
import com.google.gadgets.GadgetFeatureType;
import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;
import com.google.gadgets.Module.ModulePrefs;

public class FeaturesPart extends AbstractFormPart {

	private ModulePrefsPage page;
	
	private Map<FeatureName, Button> buttonMap;
	
	private ObjectFactory objectFactory;
	
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
		objectFactory = new ObjectFactory();
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
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (JAXBElement<?> element : elements) {
			Object value = element.getValue();
			if (value instanceof GadgetFeatureType) {
				GadgetFeatureType type = (GadgetFeatureType)value;
				String featureRealName = type.getFeature();
				FeatureName feature = FeatureName.getFeatureName(featureRealName);
				Button button = buttonMap.get(feature);
				if (button != null) {
					button.setSelection(true);
				}
			}
		}
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
		layout.numColumns = 4;
		sectionPanel.setLayout(layout);
		section.setClient(sectionPanel);
		sectionPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button opensocial08Button = createCheckbox(sectionPanel, "OpenSocial v0.8", toolkit);
		buttonMap.put(FeatureName.OPENSOCIAL_0_8, opensocial08Button);
		Button opensocial07Button = createCheckbox(sectionPanel, "OpenSocial v0.7", toolkit);
		buttonMap.put(FeatureName.OPENSOCIAL_0_7, opensocial07Button);
		Button pubsubButton = createCheckbox(sectionPanel, "PubSub", toolkit);
		buttonMap.put(FeatureName.PUBSUB, pubsubButton);
		Button viewsButton = createCheckbox(sectionPanel, "Views", toolkit);
		buttonMap.put(FeatureName.VIEWS, viewsButton);
		Button flashButton = createCheckbox(sectionPanel, "Flash", toolkit);
		buttonMap.put(FeatureName.FLASH, flashButton);
		Button skinsButton = createCheckbox(sectionPanel, "Skins", toolkit);
		buttonMap.put(FeatureName.SKINS, skinsButton);
		Button dynamicHeightButton = createCheckbox(sectionPanel, "Dynamic Height", toolkit);
		buttonMap.put(FeatureName.DYNAMIC_HEIGHT, dynamicHeightButton);
		Button setTitleButton = createCheckbox(sectionPanel, "Set Title", toolkit);
		buttonMap.put(FeatureName.SET_TITLE, setTitleButton);
		Button miniMessageButton = createCheckbox(sectionPanel, "Mini Message", toolkit);
		buttonMap.put(FeatureName.MINI_MESSAGE, miniMessageButton);
		Button tabsButton = createCheckbox(sectionPanel, "Tabs", toolkit);
		buttonMap.put(FeatureName.TABS, tabsButton);
	}
	
	private Button createCheckbox(Composite parent, String text, FormToolkit toolkit) {
		Button button = toolkit.createButton(parent, text, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setFont(parent.getFont());
		button.addSelectionListener(selectionListener);
		return button;
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		if (!onSave) {
			return;
		} else {
			setValuesToModule();
		}
	}

	private void setValuesToModule() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> requireOrOptionalOrPreload = modulePrefs.getRequireOrOptionalOrPreload();
		requireOrOptionalOrPreload.clear();
		Set<Entry<FeatureName,Button>> set = buttonMap.entrySet();
		for (Entry<FeatureName, Button> entry : set) {
			FeatureName featureName = entry.getKey();
			Button button = entry.getValue();
			if (button.getSelection()) {
				GadgetFeatureType featureType = objectFactory.createGadgetFeatureType();
				featureType.setFeature(featureName.toString());
				JAXBElement<GadgetFeatureType> require = objectFactory.createModuleModulePrefsRequire(featureType);
				requireOrOptionalOrPreload.add(require);
			}
		}
	}

}