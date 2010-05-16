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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.Messages;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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

/**
 * Dialog for running external application.
 */
public class RunExternalApplicationDialog extends TitleAreaDialog {

    private static final String DEV_APP = "http://osda.appspot.com/gadget/osda-0.8.xml";

    private static final String PREF_URL = "pref_url_for_external";
    private static final String PREF_VIEW = "pref_view_for_external";
    private static final String PREF_OWNER = "pref_owner_for_external";
    private static final String PREF_VIEWER = "pref_viewer_for_external";
    private static final String PREF_WIDTH = "pref_width_for_external";
    private static final String PREF_COUNTRY = "pref_country_for_external";
    private static final String PREF_LANG = "pref_lang_for_external";
    private static final String PREF_MEASURE_PERFORMANCE = "pref_measure_performance_for_external";
    private static final String PREF_NOT_USE_SECURITY_TOKEN = "pref_not_use_security_token";

    private List<Person> people;

    private String url;
    private String view;
    private String owner;
    private String viewer;
    private String width;
    private String country;
    private String language;
    private boolean notUseSecurityToken;
    private boolean measurePerformance;

    private Combo urlCombo;
    private Combo viewKind;
    private Combo owners;
    private Combo viewers;
    private Spinner widths;
    private Combo countries;
    private Combo languages;
    private Button notUseSecurityTokenCheck;
    private Button measurePerformanceCheck;

    private List<String> urls = new ArrayList<String>();

    public RunExternalApplicationDialog(Shell shell, List<Person> people) {
        super(shell);
        this.people = people;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("Run external application");
        setMessage("Please input and select some information.");
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        panel.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        panel.setLayoutData(layoutData);
        //
        Label label = new Label(panel, SWT.NONE);
        label.setText("Gadget XML URL:");
        urlCombo = new Combo(panel, SWT.DROP_DOWN);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 3;
        urlCombo.setLayoutData(layoutData);
        //
        label = new Label(panel, SWT.NONE);
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
        notUseSecurityTokenCheck.setText("This is not a social application.");
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        notUseSecurityTokenCheck.setLayoutData(layoutData);
        //
        measurePerformanceCheck = new Button(panel, SWT.CHECK);
        measurePerformanceCheck.setText(Messages.pref01);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        measurePerformanceCheck.setLayoutData(layoutData);
        //
        setDefaultValues();
        //
        return composite;
    }

    private void setDefaultValues() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        boolean measurePerformance = store.getBoolean(PREF_MEASURE_PERFORMANCE);
        measurePerformanceCheck.setSelection(measurePerformance);
        measurePerformanceCheck.notifyListeners(SWT.Selection, null);

        String joinedUrls = store.getString(PREF_URL);
        if (StringUtils.isNotEmpty(joinedUrls)) {
            String[] urls = StringUtils.split(joinedUrls, "|");
            Arrays.sort(urls);
            for (String url : urls) {
                urlCombo.add(url);
                this.urls.add(url);
            }
        } else {
            urlCombo.add(DEV_APP);
            this.urls.add(DEV_APP);
        }
        String prevCountry = store.getString(PREF_COUNTRY);
        if (StringUtils.isNotEmpty(prevCountry) && StringUtils.isNumeric(prevCountry)) {
            countries.select(Integer.parseInt(prevCountry));
        }
        String prevLang = store.getString(PREF_LANG);
        if (StringUtils.isNotEmpty(prevLang) && StringUtils.isNumeric(prevLang)) {
            languages.select(Integer.parseInt(prevLang));
        }
        String prevOwner = store.getString(PREF_OWNER);
        if (StringUtils.isNotEmpty(prevOwner) && StringUtils.isNumeric(prevOwner)) {
            owners.select(Integer.parseInt(prevOwner));
        }
        String prevViewer = store.getString(PREF_VIEWER);
        if (StringUtils.isNotEmpty(prevViewer) && StringUtils.isNumeric(prevViewer)) {
            viewers.select(Integer.parseInt(prevViewer));
        }
        String prevView = store.getString(PREF_VIEW);
        if (StringUtils.isNotEmpty(prevView) && StringUtils.isNumeric(prevView)) {
            viewKind.select(Integer.parseInt(prevView));
        }
        String prevWidth = store.getString(PREF_WIDTH);
        if (StringUtils.isNotEmpty(prevWidth) && StringUtils.isNumeric(prevWidth)) {
            widths.setSelection(Integer.parseInt(prevWidth));
        }
        String prevNotUseSecurityToken = store.getString(PREF_NOT_USE_SECURITY_TOKEN);
        if (StringUtils.isNotEmpty(prevNotUseSecurityToken)) {
            notUseSecurityTokenCheck.setSelection(Boolean.parseBoolean(prevNotUseSecurityToken));
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, 400);
    }

    @Override
    protected void okPressed() {
        notUseSecurityToken = notUseSecurityTokenCheck.getSelection();

        int viewerIndex = viewers.getSelectionIndex();
        int ownerIndex = owners.getSelectionIndex();
        if (!notUseSecurityToken && ((viewerIndex == -1) || (ownerIndex == -1))) {
            setErrorMessage("Owner or Viewer not selected.");
            return;
        }

        url = urlCombo.getText();
        view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
        if (viewerIndex != -1)
            viewer = viewers.getItem(viewerIndex);
        if (ownerIndex != -1)
            owner = owners.getItem(ownerIndex);
        width = widths.getText();
        measurePerformance = measurePerformanceCheck.getSelection();
        country = countries.getText();
        country = country.substring(country.indexOf('(') + 1, country.length() - 1);
        language = languages.getText();
        language = language.substring(language.indexOf('(') + 1, language.length() - 1);
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if (!urls.contains(url)) {
            urls.add(url);
        }
        String joinedUrls = StringUtils.join(urls, "|");
        store.setValue(PREF_URL, joinedUrls);
        store.setValue(PREF_COUNTRY, String.valueOf(countries.getSelectionIndex()));
        store.setValue(PREF_LANG, String.valueOf(languages.getSelectionIndex()));
        store.setValue(PREF_OWNER, String.valueOf(owners.getSelectionIndex()));
        store.setValue(PREF_VIEWER, String.valueOf(viewers.getSelectionIndex()));
        store.setValue(PREF_VIEW, String.valueOf(viewKind.getSelectionIndex()));
        store.setValue(PREF_WIDTH, String.valueOf(widths.getSelection()));
        store.setValue(PREF_MEASURE_PERFORMANCE, measurePerformanceCheck.getSelection());
        store.setValue(PREF_NOT_USE_SECURITY_TOKEN, notUseSecurityTokenCheck.getSelection());
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

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public boolean isMeasurePerformance() {
        return measurePerformance;
    }

    public boolean isNotUseSecurityToken() {
        return notUseSecurityToken;
    }

}
