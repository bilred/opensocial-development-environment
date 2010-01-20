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
package com.googlecode.osde.internal.ui.views.appdata;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddAppDataDialog extends TitleAreaDialog {

	private Text keyText;
	private Text valueText;
	
	private String key;
	private String value;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};

	public AddAppDataDialog(Shell shell) {
		super(shell);
	}
	
	private boolean validate() {
		String id = keyText.getText();
		if (StringUtils.isEmpty(id)) {
			setMessage("Please fill the key field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		String displayName = valueText.getText();
		if (StringUtils.isEmpty(displayName)) {
			setMessage("Please fill the value field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		setMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add new application data");
		setMessage("Please input key and value.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("Key:");
		keyText = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		keyText.setLayoutData(layoutData);
		keyText.addModifyListener(modifyListener);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Value:");
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		layoutData.verticalAlignment = 5;
		label.setLayoutData(layoutData);
		valueText = new Text(panel, SWT.MULTI | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		valueText.setLayoutData(layoutData);
		valueText.addModifyListener(modifyListener);
		//
		return composite;
	}

	@Override
	protected void okPressed() {
		key = keyText.getText();
		value = valueText.getText();
		setReturnCode(OK);
		close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
