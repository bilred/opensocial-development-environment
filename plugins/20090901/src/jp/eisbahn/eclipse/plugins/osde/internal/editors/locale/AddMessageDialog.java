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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import com.google.api.translate.Language;
import com.google.api.translate.Translator;
import com.google.gadgets.model.Module.ModulePrefs.Locale;

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
			if (e.getSource() == nameField) {
				// TODO: integrate with Google Translate
			}
			validate();
		}
	};

	private Text nameField;
	private Text contentField;
	
	private String messageName;
	private String messageContent;

	private Locale locale;
	
	public AddMessageDialog(Shell shell) {
		super(shell);
	}

	public AddMessageDialog(Shell shell, Locale locale) {
		super(shell);
		this.locale = locale;
	}

	private boolean validate() {
		String name = nameField.getText();
		String content = contentField.getText();
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(content)) {
			setMessage("Please fill in the name and the content", IMessageProvider.ERROR);
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
		setTitle("Add Localized Messages");
		setMessage("Please type in the message and its translations");
		
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		panel.setLayoutData(layoutData);

		Label label = new Label(panel, SWT.NONE);
		label.setText("Message Name:");
		nameField = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		nameField.setLayoutData(layoutData);
		nameField.addModifyListener(modifyListener);

		label = new Label(panel, SWT.NONE);
		label.setText("Message Content:");
		contentField = new Text(panel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		contentField.setLayoutData(layoutData);
		contentField.addModifyListener(modifyListener);
		
		return composite;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void okPressed() {
		messageName = nameField.getText();
		messageContent = contentField.getText();
		setReturnCode(OK);
		close();
	}
	
	public String getMessageName() {
		return messageName;
	}
	
	public String getMessageContent() {
		return messageContent;
	}
	
}
