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
package jp.eisbahn.eclipse.plugins.osde.internal.views;

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

public class NewPersonDialog extends TitleAreaDialog {

	private Text idText;
	private Text displayNameText;
	
	private String id;
	private String displayName;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};

	public NewPersonDialog(Shell shell) {
		super(shell);
	}
	
	private boolean validate() {
		String id = idText.getText();
		if (StringUtils.isEmpty(id)) {
			setMessage("Please fill the ID field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		String displayName = displayNameText.getText();
		if (StringUtils.isEmpty(displayName)) {
			setMessage("Please fill the Display name field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		setMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create new person");
		setMessage("Please input Person ID and display name.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("ID:");
		idText = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		idText.setLayoutData(layoutData);
		idText.addModifyListener(modifyListener);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Display name:");
		displayNameText = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		displayNameText.setLayoutData(layoutData);
		displayNameText.addModifyListener(modifyListener);
		//
		return composite;
	}

	@Override
	protected void okPressed() {
		id = idText.getText();
		displayName = displayNameText.getText();
		setReturnCode(OK);
		close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

}
