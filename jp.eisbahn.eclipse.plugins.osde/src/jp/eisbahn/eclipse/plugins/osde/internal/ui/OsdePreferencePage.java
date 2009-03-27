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

import java.io.IOException;
import java.net.MalformedURLException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.OsdeConfig;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.DatabaseLaunchConfigurationCreator;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class OsdePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Combo languages;
	private Combo countries;
	private Text databaseDirText;
	private Text jettyDirText;
	private Button internalDatabaseRadio;
	private Button externalDatabaseRadio;
	private Combo databaseTypeCombo;
	private Text hostText;
	private Text portText;
	private Text usernameText;
	private Text passwordText;
	private Button databaseBrowseButton;
	private Text nameText;

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
		//
		group = new Group(composite, SWT.NONE);
		group.setText("Web server settings");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(layoutData);
		group.setLayout(new GridLayout(3, false));
		//
		Label jettyDirLabel = new Label(group, SWT.NONE);
		jettyDirLabel.setText("Jetty context directory:");
		jettyDirText = new Text(group, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		jettyDirText.setLayoutData(layoutData);
		Button jettyBrowseButton = new Button(group, SWT.PUSH);
		jettyBrowseButton.setText("Browse...");
		jettyBrowseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Context directory");
				dialog.setMessage("Please select the directory to store the Jetty context files.");
				String dir = dialog.open();
				if (dir != null) {
					jettyDirText.setText(dir);
				}
			}
		});
		//
		Label noticeLabel = new Label(group, SWT.NONE);
		noticeLabel.setText("You need to restart Eclipse when this setting is changed.");
		layoutData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		layoutData.horizontalSpan = 3;
		noticeLabel.setLayoutData(layoutData);
		//
		//
		group = new Group(composite, SWT.NONE);
		group.setText("Database settings");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(layoutData);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 2;
		group.setLayout(layout);
		//
		internalDatabaseRadio = new Button(group, SWT.RADIO);
		internalDatabaseRadio.setText("Use the internal H2 Database.");
		internalDatabaseRadio.addSelectionListener(new DatabaseRadioSelectionListener());
		//
		Composite internalDatabasePanel = new Composite(group, SWT.NONE);
		layout = new GridLayout(3, false);
		layout.verticalSpacing = 2;
		internalDatabasePanel.setLayout(layout);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalIndent = 20;
		internalDatabasePanel.setLayoutData(layoutData);
		//
		Label databaseDirLabel = new Label(internalDatabasePanel, SWT.NONE);
		databaseDirLabel.setText("Shindig Database directory:");
		databaseDirText = new Text(internalDatabasePanel, SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		databaseDirText.setLayoutData(layoutData);
		databaseBrowseButton = new Button(internalDatabasePanel, SWT.PUSH);
		databaseBrowseButton.setText("Browse...");
		databaseBrowseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Database directory");
				dialog.setMessage("Please select the directory to store the Shindig database files.");
				String dir = dialog.open();
				if (dir != null) {
					databaseDirText.setText(dir);
				}
			}
		});
		//
		externalDatabaseRadio = new Button(group, SWT.RADIO);
		externalDatabaseRadio.setText("Use the external database.");
		externalDatabaseRadio.addSelectionListener(new DatabaseRadioSelectionListener());
		//
		Composite externalDatabasePanel = new Composite(group, SWT.NONE);
		layout = new GridLayout(4, false);
		layout.verticalSpacing = 2;
		externalDatabasePanel.setLayout(layout);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalIndent = 20;
		externalDatabasePanel.setLayoutData(layoutData);
		//
		Label typeLabel = new Label(externalDatabasePanel, SWT.NONE);
		typeLabel.setText("Database type:");
		databaseTypeCombo = new Combo(externalDatabasePanel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		databaseTypeCombo.setLayoutData(layoutData);
		databaseTypeCombo.add("MySQL");
		databaseTypeCombo.add("Oracle");
		databaseTypeCombo.select(0);
		//
		Label nameLabel = new Label(externalDatabasePanel, SWT.NONE);
		nameLabel.setText("Database name:");
		nameText = new Text(externalDatabasePanel, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(layoutData);
		//
		Label hostLabel = new Label(externalDatabasePanel, SWT.NONE);
		hostLabel.setText("Host name:");
		hostText = new Text(externalDatabasePanel, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		hostText.setLayoutData(layoutData);
		//
		Label portLabel = new Label(externalDatabasePanel, SWT.NONE);
		portLabel.setText("Port number:");
		portText = new Text(externalDatabasePanel, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		portText.setLayoutData(layoutData);
		//
		Label usernameLabel = new Label(externalDatabasePanel, SWT.NONE);
		usernameLabel.setText("User name:");
		usernameText = new Text(externalDatabasePanel, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		usernameText.setLayoutData(layoutData);
		//
		Label passwordLabel = new Label(externalDatabasePanel, SWT.NONE);
		passwordLabel.setText("Password:");
		passwordText = new Text(externalDatabasePanel, SWT.SINGLE | SWT.BORDER);
		passwordText.setEchoChar('*');
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(layoutData);
		//
		//
		createSeparator(composite);
		//
		Activator activator = Activator.getDefault();
		String version = (String)activator.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		String name = (String)activator.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_NAME);
		Label label = new Label(composite, SWT.NONE);
		label.setText(name + " Version " + version);
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
		try {
			storeValues();
			Activator activator = Activator.getDefault();
			(new DatabaseLaunchConfigurationCreator()).create(activator.getStatusMonitor());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void storeValues() {
		OsdeConfig config = new OsdeConfig();
		String country = countries.getItem(countries.getSelectionIndex());
		config.setDefaultCountry(country.substring(country.indexOf('(') + 1, country.length() - 1));
		String language = languages.getItem(languages.getSelectionIndex());
		config.setDefaultLanguage(language.substring(language.indexOf('(') + 1, language.length() - 1));
		config.setDatabaseDir(databaseDirText.getText());
		config.setJettyDir(jettyDirText.getText());
		config.setUseInternalDatabase(internalDatabaseRadio.getSelection());
		config.setExternalDatabaseHost(hostText.getText());
		config.setExternalDatabasePassword(passwordText.getText());
		config.setExternalDatabasePort(portText.getText());
		config.setExternalDatabaseUsername(usernameText.getText());
		config.setExternalDatabaseType(databaseTypeCombo.getText());
		config.setExternalDatabaseName(nameText.getText());
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
		databaseDirText.setText(config.getDatabaseDir());
		jettyDirText.setText(config.getJettyDir());
		internalDatabaseRadio.setSelection(config.isUseInternalDatabase());
		externalDatabaseRadio.setSelection(!config.isUseInternalDatabase());
		for (int i = 0; i < databaseTypeCombo.getItemCount(); i++) {
			if (databaseTypeCombo.getItem(i).equals(config.getExternalDatabaseType())) {
				databaseTypeCombo.select(i);
				break;
			}
		}
		hostText.setText(config.getExternalDatabaseHost());
		portText.setText(config.getExternalDatabasePort());
		usernameText.setText(config.getExternalDatabaseUsername());
		passwordText.setText(config.getExternalDatabasePassword());
		nameText.setText(config.getExternalDatabaseName());
		//
		changeDatabaseControlEnabled();
	}
	
	private class DatabaseRadioSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			changeDatabaseControlEnabled();
		}

	}

	private void changeDatabaseControlEnabled() {
		boolean selection = internalDatabaseRadio.getSelection();
		databaseDirText.setEnabled(selection);
		databaseBrowseButton.setEnabled(selection);
		hostText.setEnabled(!selection);
		portText.setEnabled(!selection);
		usernameText.setEnabled(!selection);
		passwordText.setEnabled(!selection);
		databaseTypeCombo.setEnabled(!selection);
		nameText.setEnabled(!selection);
	}
	
}