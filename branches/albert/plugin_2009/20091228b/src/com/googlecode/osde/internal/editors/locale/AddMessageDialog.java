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
package com.googlecode.osde.internal.editors.locale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddMessageDialog extends TitleAreaDialog {
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};

	private Text nameText;
	private Map<LocaleModel, Text> contentTextMap;
	
	private String name;
	private Map<LocaleModel, String> contentMap;

	private List<LocaleModel> localeModels;

	private LocaleModel localeModel;
	
	public AddMessageDialog(Shell shell, List<LocaleModel> localeModels, LocaleModel localeModel) {
		super(shell);
		this.localeModels = localeModels;
		this.localeModel = localeModel;
		contentTextMap = new HashMap<LocaleModel, Text>();
		contentMap = new HashMap<LocaleModel, String>();
	}
	
	private boolean validate() {
		String name = nameText.getText();
		if (StringUtils.isEmpty(name)) {
			setMessage("Please fill the name field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		setMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(500, super.getInitialSize().y);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add the localized message");
		setMessage("Please input the name and content.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("Name:");
		nameText = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(layoutData);
		nameText.addModifyListener(modifyListener);
		//
		label = new Label(panel, SWT.NONE);
		label.setText(getLabelText(localeModel));
		Text text = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(layoutData);
		text.addModifyListener(modifyListener);
		contentTextMap.put(localeModel, text);
		//
		for (LocaleModel model : localeModels) {
			if (!localeModel.equals(model)) {
				label = new Label(panel, SWT.NONE);
				label.setText(getLabelText(model));
				text = new Text(panel, SWT.BORDER);
				layoutData = new GridData(GridData.FILL_HORIZONTAL);
				text.setLayoutData(layoutData);
				text.addModifyListener(modifyListener);
				contentTextMap.put(model, text);
			}
		}
		//
		return composite;
	}
	
	private String getLabelText(LocaleModel localeModel) {
		String country = localeModel.getCountry();
		country = StringUtils.isEmpty(country) ? "(any)" : country;
		String lang = localeModel.getLang();
		lang = StringUtils.isEmpty(lang) ? "(any)" : lang;
		return lang + "_" + country;
	}

	@Override
	protected void okPressed() {
		name = nameText.getText();
		for (Map.Entry<LocaleModel, Text> entry : contentTextMap.entrySet()) {
			contentMap.put(entry.getKey(), entry.getValue().getText());
		}
		setReturnCode(OK);
		close();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	public String getName() {
		return name;
	}
	
	public Map<LocaleModel, String> getContentMap() {
		return contentMap;
	}

}
