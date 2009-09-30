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

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.model.Module;
import com.google.gadgets.model.Module.ModulePrefs;
import com.google.gadgets.model.Module.ModulePrefs.Icon;

public class IconPart extends AbstractFormPart {

	private ModulePrefsPage page;
	
	private boolean initializing;

	private String encodedIcon;

	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			setEnabledControls();
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
	private Button urlRadio;
	private Text urlText;
	private Button base64Radio;
	private Text base64Text;
	private Button browseButton;
	private Text base64TypeText;

	public IconPart(ModulePrefsPage page) {
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
		setEnabledControls();
		initializing = false;
	}
	
	private void setEnabledControls() {
		boolean use = useButton.getSelection();
		urlRadio.setEnabled(use);
		urlText.setEnabled(use);
		base64Radio.setEnabled(use);
		base64TypeText.setEnabled(use);
		base64Text.setEnabled(use);
		browseButton.setEnabled(use);
		if (use) {
			boolean url = urlRadio.getSelection();
			urlText.setEnabled(url);
			boolean base64 = base64Radio.getSelection();
			base64TypeText.setEnabled(base64);
			base64Text.setEnabled(base64);
			browseButton.setEnabled(base64);
		}
	}
	
	private void displayInitialValue() {
		useButton.setSelection(false);
		urlText.setText("");
		base64TypeText.setText("");
		base64Text.setText("");
		Module module = getModule();
		if (module != null) {
			ModulePrefs modulePrefs = module.getModulePrefs();
			List<Object> elements = modulePrefs.getRequireOrOptionalOrPreload();
			for (Object value : elements) {
				if (value instanceof Icon) {
					useButton.setSelection(true);
					Icon icon = (Icon)value;
					String mode = icon.getMode();
					if (StringUtils.isNotEmpty(mode)) {
						urlRadio.setSelection(false);
						base64Radio.setSelection(true);
						base64TypeText.setText(Gadgets.trim(icon.getType()));
						encodedIcon = Gadgets.trim(icon.getValue());
						base64Text.setText(encodedIcon.substring(0, 20) + "...");
					} else {
						urlRadio.setSelection(true);
						base64Radio.setSelection(false);
						urlText.setText(Gadgets.trim(icon.getValue()));
					}
					break;
				}
			}
		}
		setEnabledControls();
	}

	private void createControls(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.TWISTIE);
		section.setText("Icon");
		section.setDescription("Define the icon for this application.");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite sectionPanel = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		sectionPanel.setLayout(layout);
		section.setClient(sectionPanel);
		sectionPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		useButton = createCheckbox(sectionPanel, "Provide the icon for this application.", toolkit);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 4;
		useButton.setLayoutData(layoutData);
		urlRadio = createRadio(sectionPanel, toolkit, "Reference the icon with URL.", 1, modifyListener, 0);
		urlRadio.addSelectionListener(selectionListener);
		urlRadio.setSelection(true);
		urlText = createText(sectionPanel, toolkit, 3, modifyListener);
		base64Radio = createRadio(sectionPanel, toolkit, "Embed the encoded icon by Base64.", 1, modifyListener, 0);
		base64Radio.addSelectionListener(selectionListener);
		base64TypeText = createText(sectionPanel, toolkit, modifyListener);
		layoutData = new GridData();
		layoutData.widthHint = 100;
		base64TypeText.setLayoutData(layoutData);
		base64TypeText.setEditable(false);
		base64Text = createText(sectionPanel, toolkit, modifyListener);
		base64Text.setEditable(false);
		browseButton = toolkit.createButton(sectionPanel, "Browse...", SWT.PUSH);
		browseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent evt) {
				FileDialog dialog = new FileDialog(page.getSite().getShell(), SWT.OPEN);
				String fullFileName = dialog.open();
				if (StringUtils.isNotEmpty(fullFileName)) {
					File file = new File(fullFileName);
					String fileName = file.getName();
					int idx = fileName.lastIndexOf('.');
					if (idx != -1) {
						String ext = fileName.substring(idx + 1);
						long length = file.length();
						if (length > (1024 * 1024)) {
							MessageDialog.openError(page.getSite().getShell(), "Error", "Too large file size (>1MB).");
						} else {
							BufferedInputStream in = null;
							ByteArrayOutputStream out = null;
							try {
								in = new BufferedInputStream(new FileInputStream(file));
								out = new ByteArrayOutputStream();
								IOUtils.copy(in, out);
								byte[] bytes = out.toByteArray();
								byte[] encoded = Base64.encodeBase64(bytes);
								encodedIcon = new String(encoded, "UTF-8");
								StringBuilder sb = new StringBuilder();
								for(int i = 0; i < encodedIcon.length(); i++) {
									if (i != 0 && i % 76 == 0) {
										sb.append("\n");
									}
									sb.append(encodedIcon.charAt(i));
								}
								encodedIcon = sb.toString();
								base64Text.setText(encodedIcon.substring(0, 20) + "...");
								base64TypeText.setText("image/" + ext);
							} catch(IOException e) {
								MessageDialog.openError(page.getSite().getShell(), "Error", e.getMessage());
							} finally {
								IOUtils.closeQuietly(in);
								IOUtils.closeQuietly(out);
							}
						}
					} else {
						MessageDialog.openError(page.getSite().getShell(), "Error", "Please select a image file.");
					}
				}
			}
		});
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
		removeAllIcons(elements);
		if (useButton.getSelection()) {
			Icon icon = new Icon();
			if (urlRadio.getSelection()) {
				icon.setValue(urlText.getText());
			} else if (base64Radio.getSelection()) {
				icon.setMode("base64");
				icon.setType(base64TypeText.getText());
				icon.setValue(encodedIcon);
			}
			elements.add(icon);
		}
	}
	
	private void removeAllIcons(List<Object> elements) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			Object value = elements.get(i);
			if (value instanceof Icon) {
				elements.remove(i);
			}
		}
	}

	public void changeModel() {
		displayInitialValue();
	}

}