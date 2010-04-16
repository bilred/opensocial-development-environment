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
package com.googlecode.osde.internal.runtime;

import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.Messages;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.utils.Logger;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Dialog for running application locally.
 */
public class RunApplicationDialog extends TitleAreaDialog {

    private static final Logger logger = new Logger(RunApplicationDialog.class);
    private static final String PREF_VIEW = "pref_view";
    private static final String PREF_OWNER = "pref_owner";
    private static final String PREF_VIEWER = "pref_viewer";
    private static final String PREF_WIDTH = "pref_width";
    private static final String PREF_COUNTRY = "pref_country";
    private static final String PREF_LANG = "pref_lang";
    private static final String PREF_NOT_USE_SECURITY_TOKEN = "pref_not_use_security_token";
    private static final String PREF_USE_EXTERNAL_BROWSER = "pref_use_external_browser";
    private static final String PREF_MEASURE_PERFORMANCE = "pref_measure_performance";

    private List<Person> people;

    private String view;
    private String owner;
    private String viewer;
    private String width;
    private String country;
    private String language;
    private boolean notUseSecurityToken;
    private boolean useExternalBrowser;
    private boolean measurePerformance;

    private Combo viewKind;
    private Combo owners;
    private Combo viewers;
    private Spinner widths;
    private Combo countries;
    private Combo languages;
    private Button notUseSecurityTokenCheck;
    private Button useExternalBrowserCheck;
    private Button measurePerformanceCheck;

    private IFile gadgetXmlFile;

    public RunApplicationDialog(Shell shell, List<Person> people, IFile gadgetXmlFile) {
        super(shell);
        this.people = people;
        this.gadgetXmlFile = gadgetXmlFile;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("Run application");
        setMessage("Please select some information.");
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        panel.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        panel.setLayoutData(layoutData);
        //
        Label label = new Label(panel, SWT.NONE);
        label.setText("View:");
        viewKind = new Combo(panel, SWT.READ_ONLY);
        viewKind.add("Canvas");
        viewKind.add("Profile");
        viewKind.add("Home");
        viewKind.add("Preview");
        viewKind.select(0);
        viewKind.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                switch (viewKind.getSelectionIndex()) {
                    case 0:
                        widths.setSelection(100);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        widths.setSelection(60);
                        break;
                }
            }
        });
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        viewKind.setLayoutData(layoutData);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("Width(%):");
        widths = new Spinner(panel, SWT.BORDER);
        widths.setMaximum(100);
        widths.setMinimum(25);
        widths.setSelection(100);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("Owner:");
        owners = new Combo(panel, SWT.READ_ONLY);
        for (Person person : people) {
            owners.add(person.getId());
        }
        owners.select(0);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        owners.setLayoutData(layoutData);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("Viewer:");
        viewers = new Combo(panel, SWT.READ_ONLY);
        for (Person person : people) {
            viewers.add(person.getId());
        }
        viewers.select(0);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        viewers.setLayoutData(layoutData);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("Language:");
        languages = new Combo(panel, SWT.READ_ONLY);
        for (int i = 1; i < OpenSocialUtil.LANGUAGES.length; i++) {
            languages.add(OpenSocialUtil.LANGUAGES[i]);
        }
        languages.select(0);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        languages.setLayoutData(layoutData);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("Country:");
        countries = new Combo(panel, SWT.READ_ONLY);
        for (int i = 1; i < OpenSocialUtil.COUNTRIES.length; i++) {
            countries.add(OpenSocialUtil.COUNTRIES[i]);
        }
        countries.select(0);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        countries.setLayoutData(layoutData);
        //
        Label separator = new Label(panel, SWT.SEPARATOR | SWT.HORIZONTAL);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        separator.setLayoutData(layoutData);
        //
        notUseSecurityTokenCheck = new Button(panel, SWT.CHECK);
        notUseSecurityTokenCheck.setText("Non-social.");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        notUseSecurityTokenCheck.setLayoutData(layoutData);
        //
        useExternalBrowserCheck = new Button(panel, SWT.CHECK);
        useExternalBrowserCheck.setText("Use an external Web browser.");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        useExternalBrowserCheck.setLayoutData(layoutData);
        //
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        if (!support.isInternalWebBrowserAvailable()) {
            useExternalBrowserCheck.setSelection(true);
            useExternalBrowserCheck.setEnabled(false);
        }
        //
        measurePerformanceCheck = new Button(panel, SWT.CHECK);
        measurePerformanceCheck.setText(Messages.pref01);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        measurePerformanceCheck.setLayoutData(layoutData);
        measurePerformanceCheck.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                boolean enabled = measurePerformanceCheck.getSelection();
                if (enabled) {
                    useExternalBrowserCheck.setSelection(true);
                    useExternalBrowserCheck.setEnabled(false);
                } else {
                    useExternalBrowserCheck.setEnabled(true);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        //
        setDefaultValues();
        //
        return composite;
    }

    private void setDefaultValues() {
        try {
            OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
            String prevCountry = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_COUNTRY));
            if (StringUtils.isNumeric(prevCountry)) {
                countries.select(Integer.parseInt(prevCountry));
            } else {
                for (int i = 0; i < countries.getItemCount(); i++) {
                    String country = countries.getItem(i);
                    if (country.substring(country.indexOf('(') + 1, country.length() - 1)
                            .equals(config.getDefaultCountry())) {
                        countries.select(i);
                        break;
                    }
                }
            }
            String prevLang = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_LANG));
            if (StringUtils.isNumeric(prevLang)) {
                languages.select(Integer.parseInt(prevLang));
            } else {
                for (int i = 0; i < languages.getItemCount(); i++) {
                    String language = languages.getItem(i);
                    if (language.substring(language.indexOf('(') + 1, language.length() - 1)
                            .equals(config.getDefaultLanguage())) {
                        languages.select(i);
                        break;
                    }
                }
            }
            String prevOwner = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_OWNER));
            if (StringUtils.isNumeric(prevOwner)) {
                owners.select(Integer.parseInt(prevOwner));
            }
            String prevViewer = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEWER));
            if (StringUtils.isNumeric(prevViewer)) {
                viewers.select(Integer.parseInt(prevViewer));
            }
            String prevUseExternalBrowser = gadgetXmlFile.getPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_ID, PREF_USE_EXTERNAL_BROWSER));
            if (StringUtils.isNotEmpty(prevUseExternalBrowser)) {
                useExternalBrowserCheck.setSelection(Boolean.parseBoolean(prevUseExternalBrowser));
            }
            String prevView = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEW));
            if (StringUtils.isNumeric(prevView)) {
                viewKind.select(Integer.parseInt(prevView));
            }
            String prevWidth = gadgetXmlFile
                    .getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_WIDTH));
            if (StringUtils.isNumeric(prevWidth)) {
                widths.setSelection(Integer.parseInt(prevWidth));
            }

            boolean prevMeasurePerformance =
                    Boolean.parseBoolean(gadgetXmlFile.getPersistentProperty(
                            new QualifiedName(Activator.PLUGIN_ID, PREF_MEASURE_PERFORMANCE)));
            measurePerformanceCheck.setSelection(prevMeasurePerformance);
            measurePerformanceCheck.notifyListeners(SWT.Selection, null);

            String prevNotUseSecurityToken = gadgetXmlFile.getPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_ID, PREF_NOT_USE_SECURITY_TOKEN));
            if (StringUtils.isNotEmpty(prevNotUseSecurityToken)) {
                notUseSecurityTokenCheck.setSelection(Boolean.parseBoolean(prevNotUseSecurityToken));
            }
        } catch (CoreException e) {
            logger.error("Setting the default values to Run Application Dialog failed.", e);
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, 400);
    }

    @Override
    protected void okPressed() {
        setErrorMessage("");
        
        notUseSecurityToken = notUseSecurityTokenCheck.getSelection();
        
        int viewerIndex = viewers.getSelectionIndex();
        int ownerIndex = owners.getSelectionIndex();
        if (!notUseSecurityToken && ((viewerIndex == -1) || (ownerIndex == -1))) {
            setErrorMessage("Owner or Viewer not selected.");
            return;
        }
        
        view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
        if (viewerIndex != -1)
            viewer = viewers.getItem(viewerIndex);
        if (ownerIndex != -1)
            owner = owners.getItem(ownerIndex);
        width = widths.getText();
        useExternalBrowser = useExternalBrowserCheck.getSelection();
        measurePerformance = measurePerformanceCheck.getSelection();
        country = countries.getText();
        country = country.substring(country.indexOf('(') + 1, country.length() - 1);
        language = languages.getText();
        language = language.substring(language.indexOf('(') + 1, language.length() - 1);
        try {
            gadgetXmlFile
                    .setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_COUNTRY),
                            String.valueOf(countries.getSelectionIndex()));
            gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_LANG),
                    String.valueOf(languages.getSelectionIndex()));
            gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_OWNER),
                    String.valueOf(ownerIndex));
            gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEWER),
                    String.valueOf(viewerIndex));
            gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEW),
                    String.valueOf(viewKind.getSelectionIndex()));
            gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_WIDTH),
                    String.valueOf(widths.getSelection()));
            gadgetXmlFile.setPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_ID, PREF_USE_EXTERNAL_BROWSER),
                    String.valueOf(useExternalBrowserCheck.getSelection()));
            gadgetXmlFile.setPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_ID, PREF_MEASURE_PERFORMANCE),
                    String.valueOf(measurePerformanceCheck.getSelection()));
            gadgetXmlFile.setPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_ID, PREF_NOT_USE_SECURITY_TOKEN),
                    String.valueOf(notUseSecurityTokenCheck.getSelection()));
        } catch (CoreException e) {
            logger.error("Setting persistent properties failed.", e);
        }

        setReturnCode(OK);
        close();
    }

    public String getView() {
        return view;
    }

    public String getOwner() {
        return owner;
    }

    public String getViewer() {
        return viewer;
    }

    public String getWidth() {
        return width;
    }

    public boolean isUseExternalBrowser() {
        return useExternalBrowser;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isMeasurePerformance() {
        return measurePerformance;
    }

    public boolean isNotUseSecurityToken() {
        return notUseSecurityToken;
    }

}
