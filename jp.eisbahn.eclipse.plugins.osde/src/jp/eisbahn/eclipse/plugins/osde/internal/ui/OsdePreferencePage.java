/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.ui;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.OsdeConfig;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class OsdePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo languages;
	private Combo countries;

	public OsdePreferencePage() {
		super();
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		//
		Group group = new Group(composite, SWT.NONE);
		group.setText("Default locale");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(layoutData);
		group.setLayout(new GridLayout(2, false));
		//
		Label languageLabel = new Label(group, SWT.NONE);
		languageLabel.setText("Language:");
		languages = new Combo(group, SWT.READ_ONLY);
		for (int i = 1; i < OpenSocialUtil.LANGUAGES.length; i++) {
			languages.add(OpenSocialUtil.LANGUAGES[i]);
		}
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		languages.setLayoutData(layoutData);
		//
		Label countryLabel = new Label(group, SWT.NONE);
		countryLabel.setText("Country:");
		countries = new Combo(group, SWT.READ_ONLY);
		for (int i = 1; i < OpenSocialUtil.COUNTRIES.length; i++) {
			countries.add(OpenSocialUtil.COUNTRIES[i]);
		}
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		countries.setLayoutData(layoutData);
		//
		createSeparator(composite);
		//
		Activator activator = Activator.getDefault();
		String version = (String)activator.getBundle().getHeaders().get(
				org.osgi.framework.Constants.BUNDLE_VERSION);
		String name = (String)activator.getBundle().getHeaders().get(
				org.osgi.framework.Constants.BUNDLE_NAME);
		Label label = new Label(composite, SWT.NONE);
		label.setText(name);
		label = new Label(composite, SWT.NONE);
		label.setText("Version " + version);
		//
		initializeValues();
		//
		return composite;
	}
	
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		separator.setLayoutData(layoutData);
	}
	
	protected void performDefaults() {
		initializeDefaults();
		super.performDefaults();
	}
	
	public boolean performOk() {
		storeValues();
		return true;
	}
	
	private void storeValues() {
		OsdeConfig config = new OsdeConfig();
		String country = countries.getItem(countries.getSelectionIndex());
		config.setDefaultCountry(country.substring(country.indexOf('(') + 1, country.length() - 1));
		String language = languages.getItem(languages.getSelectionIndex());
		config.setDefaultLanguage(language.substring(language.indexOf('(') + 1, language.length() - 1));
		Activator.getDefault().storePreferences(config);
}
	
	private void initializeValues() {
		OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
		setConfigurationToDisplay(config);
	}
	
	private void initializeDefaults() {
		OsdeConfig config = Activator.getDefault().getDefaultOsdeConfiguration();
		setConfigurationToDisplay(config);
}
	
	private void setConfigurationToDisplay(OsdeConfig config) {
		for (int i = 0; i < countries.getItemCount(); i++) {
			String country = countries.getItem(i);
			if (country.substring(country.indexOf('(') + 1, country.length() - 1).equals(config.getDefaultCountry())) {
				countries.select(i);
				break;
			}
		}
		for (int i = 0; i < languages.getItemCount(); i++) {
			String language = languages.getItem(i);
			if (language.substring(language.indexOf('(') + 1, language.length() - 1).equals(config.getDefaultLanguage())) {
				languages.select(i);
				break;
			}
		}
	}

}