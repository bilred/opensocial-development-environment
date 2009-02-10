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

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.GadgetFeatureType;
import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;
import com.google.gadgets.GadgetFeatureType.Param;
import com.google.gadgets.Module.ModulePrefs;

public class ContentRewritePart extends AbstractFormPart {

	private ModulePrefsPage page;
	
	private ObjectFactory objectFactory;
	
	private boolean initializing;
	
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			if (!initializing) {
				markDirty();
			}
		}
	};
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			if (!initializing) {
				markDirty();
			}
		}
	};

	private Button useButton;
	
	public ContentRewritePart(ModulePrefsPage page) {
		this.page = page;
		objectFactory = new ObjectFactory();
	}
	
	private Module getModule() {
		return page.getModule();
	}
	
	@Override
	public void initialize(IManagedForm form) {
		initializing = true;
		super.initialize(form);
		createControls(form);
		displayInitialValue();
		initializing = false;
	}
	
	private void displayInitialValue() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (JAXBElement<?> element : elements) {
			Object value = element.getValue();
			if (value instanceof GadgetFeatureType) {
				GadgetFeatureType type = (GadgetFeatureType)value;
				QName name = element.getName();
				String featureRealName = type.getFeature();
				if (name.equals("Optional") && featureRealName.equals("content-rewrite")) {
					useButton.setSelection(true);
					List<Param> params = type.getParam();
					for (Param param : params) {
						if (param.getName().equals("include-urls")) {
							
						}
					}
				} else {
					useButton.setSelection(false);
				}
			}
		}
	}

	private void createControls(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE);
		section.setText("Content rewrite");
		section.setDescription("Content-rewrite feature allows you to control the cache configuration.");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite sectionPanel = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		sectionPanel.setLayout(layout);
		section.setClient(sectionPanel);
		sectionPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		useButton = createCheckbox(sectionPanel, "Use content-rewrite", toolkit);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 4;
		useButton.setLayoutData(layoutData);
		createLabel(sectionPanel, toolkit, "Include URLs:");
		createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Exclude URLs:");
		createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Include Tags:");
		createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Expires:");
		Spinner expiresSpinner = new Spinner(sectionPanel, SWT.NONE);
		expiresSpinner.setIncrement(100);
		expiresSpinner.setMinimum(0);
		expiresSpinner.setMaximum(604800);
		expiresSpinner.setSelection(86400);
	}
	
	private Button createCheckbox(Composite parent, String text, FormToolkit toolkit) {
		Button button = toolkit.createButton(parent, text, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setFont(parent.getFont());
		button.addSelectionListener(selectionListener);
		return button;
	}
	
	public void setValuesToModule() {
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
	}
	
	public void changeModel() {
		displayInitialValue();
	}

}