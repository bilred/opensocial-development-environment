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

package jp.eisbahn.eclipse.plugins.osde.internal.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <code>FieldEditorPreferencePage</code>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */

public class OsdePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public OsdePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configurations for OSDE, Shindig, and Database");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		Display display = getShell().getDisplay();
		
		// Group for Locale
		Group localeGroup = new Group(getFieldEditorParent(), SWT.NONE);
		localeGroup.setText("Locale Settings");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		localeGroup.setLayoutData(layoutData);
		localeGroup.setLayout(new GridLayout(2, false));
		
		// Construct Country Field
		final int NUM_COUNTRIES = OpenSocialUtil.COUNTRIES.length;
		final int NUM_COLUMNS = 2; // one for key and one for value
		String[][] countryKeysAndValues = new String[NUM_COUNTRIES][NUM_COLUMNS];
		for (int i = 0; i < NUM_COUNTRIES; ++i) {
			String key = OpenSocialUtil.COUNTRIES[i];
			String value = key.substring(key.indexOf('(')+1, key.length()-1);
			countryKeysAndValues[i][0] = key;
			countryKeysAndValues[i][1] = value;
		}
		ComboFieldEditor countryFieldEditor = new ComboFieldEditor(PreferenceConstants.DEFAULT_COUNTRY,
				                                  				   "Country",
				                                  				   countryKeysAndValues,
				                                  				   localeGroup);
		addField(countryFieldEditor);
		
		// Construct Language Field
		final int NUM_LANGUAGES = OpenSocialUtil.LANGUAGES.length;
		String[][] languageKeysAndValues = new String[NUM_LANGUAGES][NUM_COLUMNS];
		for (int i = 0; i < NUM_LANGUAGES; ++i) {
			String key = OpenSocialUtil.LANGUAGES[i];
			String value = key.substring(key.indexOf('(')+1, key.length()-1);
			languageKeysAndValues[i][0] = key;
			languageKeysAndValues[i][1] = value;
		}
		ComboFieldEditor languageFieldEditor = new ComboFieldEditor(PreferenceConstants.DEFAULT_LANGUAGE,
				                                   					"Language",
				                                   					languageKeysAndValues,
				                                   					localeGroup);
		addField(languageFieldEditor);
		
		// Web Server Settings
		Group webServerGroup = new Group(getFieldEditorParent(), SWT.NONE);
		webServerGroup.setText("Web Server Settings");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		webServerGroup.setLayoutData(layoutData);
		webServerGroup.setLayout(new GridLayout(2, false));
		
		addField(new DirectoryFieldEditor(PreferenceConstants.JETTY_DIR,
				   						  "Jetty directory",
				   						  webServerGroup));
		
		// Database group for database related settings
		Group databaseGroup = new Group(getFieldEditorParent(), SWT.NONE);
		databaseGroup.setText("External Database Settings");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		databaseGroup.setLayoutData(layoutData);
		databaseGroup.setLayout(new GridLayout(2, false));
		
		BooleanFieldEditor useInternalDatabase = new BooleanFieldEditor(PreferenceConstants.USE_INTERNAL_DATABASE,
				 									 					"Use internal H2 database",
				 									 					databaseGroup);
		addField(useInternalDatabase);
		
		Label internalDatabaseLabel = new Label(databaseGroup, SWT.NONE);
		internalDatabaseLabel.setText("Please Note: If you choose to use internal database,\n" +
									  "You don't have to configure the following settings for external database");
		internalDatabaseLabel.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		internalDatabaseLabel.setLayoutData(layoutData);
		
		RadioGroupFieldEditor externalDatabaseType = new RadioGroupFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_TYPE,
														 "External database type",
														 1,
														 new String[][] {{"MySQL", "MySQL"},{"Oracle", "Oracle"}},
														 databaseGroup);
		addField(externalDatabaseType);	
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_HOST,
				   					   "Database host",
				   					   databaseGroup));
		addField(new IntegerFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_PORT,
				   						"Database port",
				   						databaseGroup));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_NAME,
				   					   "Database user name",
				   					   databaseGroup));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_PASSWORD,
				   					   "Database password",
				   					   databaseGroup));
		addField(new DirectoryFieldEditor(PreferenceConstants.DATABASE_DIR,
				   					   "Database directory",
				   					   databaseGroup));
		
		// Group for other settings
		Group otherGroup = new Group(getFieldEditorParent(), SWT.NONE);
		otherGroup.setText("Other Settings");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		otherGroup.setLayoutData(layoutData);
		otherGroup.setLayout(new GridLayout(2, false));
		addField(new StringFieldEditor(PreferenceConstants.DOCS_SITE_MAP,
				   					   "Docs site map",
				   					   otherGroup));
		
		// Put version information at the bottom of the preference page
		Group versionInfoGroup = new Group(getFieldEditorParent(), SWT.NONE);
		versionInfoGroup.setText("Version Information");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		versionInfoGroup.setLayoutData(layoutData);
		versionInfoGroup.setLayout(new GridLayout(2, false));
		
		Activator activator = Activator.getDefault();
		String osdeVersion = (String)activator.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		String name = (String)activator.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_NAME);
		String shindigVersion = (String)activator.getBundle().getHeaders().get("Bundle-Shindig-Revision");
		
		Label osdeVersionLabel = new Label(versionInfoGroup, SWT.NONE);
		osdeVersionLabel.setText(name + " Version: " + osdeVersion);
		Label shindigVersionLabel = new Label(versionInfoGroup, SWT.NONE);
		shindigVersionLabel.setText("Apache Shindig Revision: " + shindigVersion);
	}

	/**
	 * The initializer does nothing here
	 */
	public void init(IWorkbench workbench) {		
	}

}