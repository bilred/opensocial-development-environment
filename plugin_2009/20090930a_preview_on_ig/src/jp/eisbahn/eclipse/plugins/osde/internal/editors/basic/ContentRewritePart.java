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

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.model.Module;
import com.google.gadgets.model.Param;
import com.google.gadgets.model.Module.ModulePrefs;
import com.google.gadgets.model.Module.ModulePrefs.Optional;

public class ContentRewritePart extends AbstractFormPart {

	private ModulePrefsPage page;
	
	private boolean initializing;
	
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			setEnabledControls(useButton.getSelection());
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

	private Text includeUrlsText;

	private Text excludeUrlsText;

	private Text includeTagsText;

	private Spinner expiresSpinner;
	
	public ContentRewritePart(ModulePrefsPage page) {
		this.page = page;
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
		setEnabledControls(useButton.getSelection());
		initializing = false;
	}
	
	private void setEnabledControls(boolean enabled) {
		includeUrlsText.setEnabled(enabled);
		excludeUrlsText.setEnabled(enabled);
		includeTagsText.setEnabled(enabled);
		expiresSpinner.setEnabled(enabled);
	}
	
	private void displayInitialValue() {
		useButton.setSelection(false);
		includeUrlsText.setText("");
		excludeUrlsText.setText("");
		includeTagsText.setText("");
		expiresSpinner.setSelection(86400);
		Module module = getModule();
		if (module != null) {
			ModulePrefs modulePrefs = module.getModulePrefs();
			List<?> elements = modulePrefs.getRequireOrOptionalOrPreload();
			for (Object element : elements) {
				if (element instanceof Optional) {
					Optional optional = (Optional)element;
					String feature = optional.getFeature();
					if (feature.equals("content-rewrite")) {
						useButton.setSelection(true);
						List<Param> params = optional.getParam();
						for (Param param : params) {
							if (param.getName().equals("include-urls")) {
								includeUrlsText.setText(Gadgets.trim(param.getValue()));
							}
							if (param.getName().equals("exclude-urls")) {
								excludeUrlsText.setText(Gadgets.trim(param.getValue()));
							}
							if (param.getName().equals("include-tags")) {
								includeTagsText.setText(Gadgets.trim(param.getValue()));
							}
							if (param.getName().equals("expires")) {
								Integer expiresValue = Gadgets.toInteger(param.getValue());
								if (expiresValue != null) {
									expiresSpinner.setSelection(expiresValue);
								} else {
									expiresSpinner.setSelection(86400);
								}
							}
						}
						return;
					}
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
		includeUrlsText = createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Exclude URLs:");
		excludeUrlsText = createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Include Tags:");
		includeTagsText = createText(sectionPanel, toolkit, modifyListener);
		createLabel(sectionPanel, toolkit, "Expires:");
		expiresSpinner = new Spinner(sectionPanel, SWT.NONE);
		expiresSpinner.setIncrement(100);
		expiresSpinner.setMinimum(0);
		expiresSpinner.setMaximum(604800);
		expiresSpinner.setSelection(86400);
		expiresSpinner.addListener(SWT.Modify, modifyListener);
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
		List<Object> elements = modulePrefs.getRequireOrOptionalOrPreload();
		Optional contentRewriteNode = getContentRewriteNode(elements);
		if (contentRewriteNode != null) {
			elements.remove(contentRewriteNode);
		}
		if (useButton.getSelection()) {
			Optional optional = new Optional();
			optional.setFeature("content-rewrite");
			List<Param> params = optional.getParam();
			String includeUrls = includeUrlsText.getText();
			if (StringUtils.isNotEmpty(includeUrls)) {
				Param param = new Param();
				param.setName("include-urls");
				param.setValue(includeUrls);
				params.add(param);
			}
			String excludeUrls = excludeUrlsText.getText();
			if (StringUtils.isNotEmpty(excludeUrls)) {
				Param param = new Param();
				param.setName("exclude-urls");
				param.setValue(excludeUrls);
				params.add(param);
			}
			String includeTags = includeTagsText.getText();
			if (StringUtils.isNotEmpty(includeTags)) {
				Param param = new Param();
				param.setName("include-tags");
				param.setValue(includeTags);
				params.add(param);
			}
			Param param = new Param();
			param.setName("expires");
			param.setValue(String.valueOf(expiresSpinner.getSelection()));
			params.add(param);
			elements.add(optional);
		}
	}
	
	private Optional getContentRewriteNode(List<?> elements) {
		for (Object value : elements) {
			if ((value instanceof Optional)
					&& ((Optional)value).getFeature().equals("content-rewrite")) {
				return (Optional)value;
			}
		}
		return null;
	}
	
	public void changeModel() {
		displayInitialValue();
	}

}