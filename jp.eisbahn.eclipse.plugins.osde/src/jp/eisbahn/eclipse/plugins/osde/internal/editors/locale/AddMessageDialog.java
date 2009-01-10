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
	private Text contentText;
	
	private String name;
	private String content;
	
	public AddMessageDialog(Shell shell) {
		super(shell);
	}
	
	private boolean validate() {
		String name = nameText.getText();
		if (StringUtils.isEmpty(name)) {
			setMessage("Please fill the name field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		String content = contentText.getText();
		if (StringUtils.isEmpty(content)) {
			setMessage("Please fill the content field.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		setMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
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
		label.setText("Content:");
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		layoutData.verticalAlignment = 5;
		label.setLayoutData(layoutData);
		contentText = new Text(panel, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		layoutData = new GridData(GridData.FILL_BOTH);
		contentText.setLayoutData(layoutData);
		contentText.addModifyListener(modifyListener);
		//
		return composite;
	}

	@Override
	protected void okPressed() {
		name = nameText.getText();
		content = contentText.getText();
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

	public String getContent() {
		return content;
	}

}
