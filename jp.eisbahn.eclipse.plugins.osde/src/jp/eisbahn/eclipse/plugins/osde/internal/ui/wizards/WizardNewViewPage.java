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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createCheckbox;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createText;

import java.util.EnumMap;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;

public class WizardNewViewPage extends WizardPage {
	
	private EnumMap<ViewName, ViewSettingPart> partMap;
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			boolean valid = validatePage();
			setPageComplete(valid);
		}
	};
	
	public WizardNewViewPage(String pageName) {
		super(pageName);
		setPageComplete(false);
		partMap = new EnumMap<ViewName, ViewSettingPart>(ViewName.class);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		TabFolder tabFolder = new TabFolder(composite, SWT.NULL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createViewControls(composite, ViewName.canvas, tabFolder);
		createViewControls(composite, ViewName.profile, tabFolder);
		createViewControls(composite, ViewName.preview, tabFolder);
		createViewControls(composite, ViewName.home, tabFolder);
		//
		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}
	
	private boolean validatePage() {
		ViewSettingPart canvas = partMap.get(ViewName.canvas);
		ViewSettingPart profile = partMap.get(ViewName.profile);
		ViewSettingPart preview = partMap.get(ViewName.preview);
		ViewSettingPart home = partMap.get(ViewName.home);
		if (canvas.getNotSupportButton().getSelection()
				&& profile.getNotSupportButton().getSelection()
				&& preview.getNotSupportButton().getSelection()
				&& home.getNotSupportButton().getSelection()) {
			setErrorMessage(null);
			setMessage("It is necessary to support at least one view.");
			return false;
		}
		Set<ViewName> keySet = partMap.keySet();
		for (ViewName viewName : keySet) {
			ViewSettingPart part = partMap.get(viewName);
			if (part.getUrlButton().getSelection()) {
				String text = part.getHrefText().getText().trim();
				if (text.length() == 0) {
					setErrorMessage(null);
					setMessage("Location URL for " + viewName.getDisplayName() + " view is empty.");
					return false;
				}
			}
		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	private void createViewControls(Composite composite, ViewName viewName, TabFolder folder) {
		TabItem item = new TabItem(folder, SWT.BORDER);
		item.setText(viewName.getDisplayName());
		ViewSettingPart part = new ViewSettingPart(folder);
		item.setControl(part.getComposite());
		partMap.put(viewName, part);
	}
	
	public EnumMap<ViewName, GadgetViewData> getInputedData() {
		EnumMap<ViewName, GadgetViewData> resultMap = new EnumMap<ViewName, GadgetViewData>(ViewName.class);
		Set<ViewName> keySet = partMap.keySet();
		for (ViewName viewName : keySet) {
			ViewSettingPart part = partMap.get(viewName);
			if (!part.getNotSupportButton().getSelection()) {
				GadgetViewData data = new GadgetViewData();
				data.setViewName(viewName);
				if (part.htmlButton.getSelection()) {
					data.setType(ViewType.html);
					data.setCreateExternalJavaScript(part.getCreateJavaScriptFileButton().getSelection());
					data.setCreateInitFunction(part.getInitFunctionButton().getSelection());
				} else if (part.urlButton.getSelection()) {
					data.setType(ViewType.url);
					data.setHref(part.getHrefText().getText().trim());
				} else {
					throw new IllegalStateException("Unknown ViewType is selected.");
				}
				resultMap.put(viewName, data);
			}
		}
		return resultMap;
	}
	
	private class ViewSettingPart {
	
		private Composite composite;
		private Button htmlButton;
		private Button createJavaScriptFileButton;
		private Button initFunctionButton;
		private Button urlButton;
		private Text hrefText;
		private Button notSupportButton;

		ViewSettingPart(Composite parent) {
			super();
			createViewControls(parent);
		}
		
		public Composite getComposite() {
			return composite;
		}

		public Button getHtmlButton() {
			return htmlButton;
		}

		public Button getCreateJavaScriptFileButton() {
			return createJavaScriptFileButton;
		}

		public Button getInitFunctionButton() {
			return initFunctionButton;
		}

		public Button getUrlButton() {
			return urlButton;
		}

		public Text getHrefText() {
			return hrefText;
		}

		public Button getNotSupportButton() {
			return notSupportButton;
		}

		private void createViewControls(Composite parent) {
			composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			htmlButton = createRadio(composite, "Type: HTML");
			htmlButton.addListener(SWT.Selection, modifyListener);
			Group htmlGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			htmlGroup.setFont(parent.getFont());
			htmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			layout = new GridLayout();
			layout.numColumns = 1;
			htmlGroup.setLayout(layout);
			htmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			createJavaScriptFileButton = createCheckbox(htmlGroup, "Create the external JavaScript file for this view.");
			createJavaScriptFileButton.addListener(SWT.Selection, modifyListener);
			initFunctionButton = createCheckbox(htmlGroup, "Generate the init() function that is called when this view is loaded.");
			initFunctionButton.addListener(SWT.Selection, modifyListener);
			createJavaScriptFileButton.setEnabled(false);
			initFunctionButton.setEnabled(false);
			createJavaScriptFileButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					boolean selection = createJavaScriptFileButton.getSelection();
					initFunctionButton.setEnabled(selection);
				}
			});
			urlButton = createRadio(composite, "Type: URL");
			urlButton.addListener(SWT.Selection, modifyListener);
			Group urlGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			urlGroup.setFont(parent.getFont());
			urlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			layout = new GridLayout();
			layout.numColumns = 2;
			urlGroup.setLayout(layout);
			urlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			createLabel(urlGroup, "Location URL:");
			hrefText = createText(urlGroup);
			hrefText.addListener(SWT.Modify, modifyListener);
			hrefText.setEnabled(false);
			notSupportButton = createRadio(composite, "Not support");
			notSupportButton.addListener(SWT.Selection, modifyListener);
			SelectionListener selectionListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					createJavaScriptFileButton.setEnabled(htmlButton.getSelection());
					initFunctionButton.setEnabled(htmlButton.getSelection() & createJavaScriptFileButton.getSelection());
					hrefText.setEnabled(urlButton.getSelection());
				}
			};
			htmlButton.addSelectionListener(selectionListener);
			urlButton.addSelectionListener(selectionListener);
			notSupportButton.addSelectionListener(selectionListener);
			notSupportButton.setSelection(true);
		}

	}

}
