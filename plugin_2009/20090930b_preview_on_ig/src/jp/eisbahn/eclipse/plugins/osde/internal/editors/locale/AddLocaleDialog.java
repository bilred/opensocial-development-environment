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

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AddLocaleDialog extends TitleAreaDialog {
	
	private Combo countryCombo;
	private Combo languageCombo;
	private Button internalButton;
	
	private String country;
	private String language;
	private boolean internal;
	
	public AddLocaleDialog(Shell shell) {
		super(shell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add the supported locale");
		setMessage("Please select the country and language.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("Country:");
		countryCombo = new Combo(panel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		countryCombo.setLayoutData(layoutData);
		for (int i = 0; i < OpenSocialUtil.COUNTRIES.length; i++) {
			countryCombo.add(OpenSocialUtil.COUNTRIES[i]);
		}
		countryCombo.select(0);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Language:");
		languageCombo = new Combo(panel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		languageCombo.setLayoutData(layoutData);
		for (int i = 0; i < OpenSocialUtil.LANGUAGES.length; i++) {
			languageCombo.add(OpenSocialUtil.LANGUAGES[i]);
		}
		languageCombo.select(0);
		//
		internalButton = new Button(panel, SWT.CHECK);
		internalButton.setText("Define a message bundle in Gadget XML file.");
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		internalButton.setLayoutData(layoutData);
		internalButton.setSelection(true);
		//
		return composite;
	}

	@Override
	protected void okPressed() {
		country = countryCombo.getText();
		if (country.equals(OpenSocialUtil.ANY)) {
			country = "ALL";
		} else {
			country = country.substring(country.indexOf('(') + 1, country.length() - 1);
		}
		language = languageCombo.getText();
		if (language.equals(OpenSocialUtil.ANY)) {
			language = "ALL";
		} else {
			language = language.substring(language.indexOf('(') + 1, language.length() - 1);
		}
		internal = internalButton.getSelection();
		setReturnCode(OK);
		close();
	}

	public String getCountry() {
		return country;
	}

	public String getLanguage() {
		return language;
	}
	
	public boolean isInternal() {
		return internal;
	}
	
}
