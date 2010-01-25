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
package com.googlecode.osde.internal.ui;

import java.io.File;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.OsdePreferencesModel;
import com.googlecode.osde.internal.common.JdkVersion;
import com.googlecode.osde.internal.shindig.DatabaseServer;
import com.googlecode.osde.internal.ui.pref_binder.ComboBinder;
import com.googlecode.osde.internal.ui.pref_binder.FirefoxTextBinder;
import com.googlecode.osde.internal.ui.pref_binder.LocaleComboBinder;
import com.googlecode.osde.internal.ui.pref_binder.PreferenceBinder;
import com.googlecode.osde.internal.ui.pref_binder.SelectionBinder;
import com.googlecode.osde.internal.ui.pref_binder.TextBinder;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Eclipse preference page for OSDE.
 */
public class OsdePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private PreferenceBinder preferenceBinder = new PreferenceBinder();
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
    private Text workDirectoryText;
    private Text loggerCfgLocationText;
    private Button compileJavaScriptCheckbox;
    private Text firefoxLocation;

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
        group.setText("General");
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(layoutData);
        group.setLayout(new GridLayout(3, false));
        //
        Label workDirLabel = new Label(group, SWT.NONE);
        workDirLabel.setText("Work directory:");
        workDirectoryText = new Text(group, SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        workDirectoryText.setLayoutData(layoutData);
        createFolderButton(group, "Work directory",
                "Please select the directory to work OSDE.", workDirectoryText);
        //
        group = new Group(composite, SWT.NONE);
        group.setText("Default locale");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
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
        createFolderButton(group, "Context directory",
                "Please select the directory to store the Jetty context files.", jettyDirText);
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
        databaseBrowseButton = createFolderButton(internalDatabasePanel,
                "Database directory",
                "Please select the directory to store the Shindig database files.",
                databaseDirText);
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
        group = new Group(composite, SWT.NONE);
        group.setText("Logger settings");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(layoutData);
        group.setLayout(new GridLayout(3, false));
        //
        Label loggerLabel = new Label(group, SWT.NONE);
        loggerLabel.setText("Logger configuration file:");
        loggerCfgLocationText = new Text(group, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        loggerCfgLocationText.setLayoutData(layoutData);
        createFileButton(group, "Please select the logger configuration file.",
                loggerCfgLocationText, "*.properties");

        group = new Group(composite, SWT.NONE);
        group.setText("Web performance tools");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(layoutData);
        group.setLayout(new GridLayout(3, false));

        new Label(group, SWT.NONE).setText("Firefox location:");
        firefoxLocation = new Text(group, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        firefoxLocation.setLayoutData(layoutData);
        createFileButton(group, "Please select the Firefox executable to launch profiler.",
                firefoxLocation);

        compileJavaScriptCheckbox = new Button(group, SWT.CHECK);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        compileJavaScriptCheckbox.setLayoutData(layoutData);
        compileJavaScriptCheckbox.setText("Compile JavaScript files (Requires JDK 6)");
        if (!JdkVersion.isAtLeastJdk6()) {
            compileJavaScriptCheckbox.setEnabled(false);
            compileJavaScriptCheckbox.setSelection(false);
        }

        createSeparator(composite);
        //
        Activator activator = Activator.getDefault();
        String version = (String) activator.getBundle().getHeaders()
                .get(org.osgi.framework.Constants.BUNDLE_VERSION);
        String name = (String) activator.getBundle().getHeaders()
                .get(org.osgi.framework.Constants.BUNDLE_NAME);
        String shindigRevision =
                (String) activator.getBundle().getHeaders().get("Bundle-Shindig-Revision");
        Label label = new Label(composite, SWT.NONE);
        label.setText(name + " Version " + version);
        label = new Label(composite, SWT.NONE);
        label.setText("Revision of Apache Shindig: " + shindigRevision);
        //
        bindUIandPreferences();
        //
        initializeValues();
        //
        return composite;
    }

    /**
     * Creates a button that, when clicked, opens a file dialog, and commits
     * commits the absolute path of a chosen file back to a text field.
     *
     * @param parent A widget containing this button.
     * @param dialogTitle The title of file dialog.
     * @param backfillText The text field which accepts the chosen file path.
     * @param extensionFilters Optional list of file extension filters used
     * by the file dialog.
     * @return A new button instance.
     */
    private Button createFileButton(Composite parent, final String dialogTitle,
            final Text backfillText, final String... extensionFilters) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell());
                dialog.setFilterExtensions(extensionFilters);
                dialog.setText(dialogTitle);
                String logCfgFile = dialog.open();
                if (logCfgFile != null) {
                    backfillText.setText(logCfgFile);
                }
            }
        });
        return button;
    }

    /**
     * Creates a button that, when clicked, opens a folder dialog, and commits
     * the absolute path of the chosen folder back to a text field.
     *
     * @param parent The widget containing this button.
     * @param dialogTitle The title of folder dialog.
     * @param dialogMessage The prompt message of folder dialog.
     * @param backfillText The text field which accepts the chosen folder path.
     * @return A new button instance.
     */
    private Button createFolderButton(Composite parent, final String dialogTitle,
            final String dialogMessage, final Text backfillText) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setText(dialogTitle);
                dialog.setMessage(dialogMessage);
                String dir = dialog.open();
                if (dir != null) {
                    backfillText.setText(dir);
                }
            }
        });
        return button;
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
        new DatabaseServer().createConfiguration();
        return true;
    }

    private void bindUIandPreferences(){
        preferenceBinder.addBinder(new LocaleComboBinder(countries, OsdeConfig.DEFAULT_COUNTRY));
        preferenceBinder.addBinder(new LocaleComboBinder(languages, OsdeConfig.DEFAULT_LANGUAGE));
        preferenceBinder.addBinder(new TextBinder(databaseDirText, OsdeConfig.DATABASE_DIR));
        preferenceBinder.addBinder(new TextBinder(jettyDirText, OsdeConfig.JETTY_DIR));
        preferenceBinder.addBinder(new TextBinder(hostText, OsdeConfig.EXTERNAL_DATABASE_HOST));
        preferenceBinder.addBinder(
                new TextBinder(passwordText, OsdeConfig.EXTERNAL_DATABASE_PASSWORD));
        preferenceBinder.addBinder(new TextBinder(portText, OsdeConfig.EXTERNAL_DATABASE_PORT));
        preferenceBinder.addBinder(
                new TextBinder(usernameText, OsdeConfig.EXTERNAL_DATABASE_USERNAME));
        preferenceBinder.addBinder(new TextBinder(nameText, OsdeConfig.EXTERNAL_DATABASE_NAME));
        preferenceBinder.addBinder(
                new TextBinder(loggerCfgLocationText, OsdeConfig.LOGGER_CONFIG_FILE));
        
        preferenceBinder.addBinder(new TextBinder(workDirectoryText,
                OsdeConfig.WORK_DIRECTORY) {
            @Override
            public void doSave(OsdePreferencesModel model) throws Exception {
                super.doSave(model);
                try {
                    new File(control.getText()).mkdirs();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        
        preferenceBinder.addBinder(new FirefoxTextBinder(firefoxLocation,
                OsdeConfig.DEFAULT_FIREFOX_LOCATION));

        preferenceBinder.addBinder(
                new ComboBinder(databaseTypeCombo, OsdeConfig.EXTERNAL_DATABASE_TYPE));

        preferenceBinder.addBinder(
                new SelectionBinder(compileJavaScriptCheckbox, OsdeConfig.COMPILE_JAVASCRIPT));
        preferenceBinder.addBinder(
                new SelectionBinder(internalDatabaseRadio, OsdeConfig.USE_INTERNAL_DATABASE));
    }

    private void storeValues() {
        preferenceBinder.doSave(Activator.getDefault().getPreferenceModel());
    }

    private void initializeValues() {
        preferenceBinder.doLoad(Activator.getDefault().getPreferenceModel());
        changeDatabaseControlEnabled();
    }

    private void initializeDefaults() {
        preferenceBinder.doLoadDefault(Activator.getDefault().getPreferenceModel());
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
