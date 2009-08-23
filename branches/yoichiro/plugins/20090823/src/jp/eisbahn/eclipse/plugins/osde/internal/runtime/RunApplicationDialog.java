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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.OsdeConfig;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

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

public class RunApplicationDialog extends TitleAreaDialog {
	
	private static final String PREF_VIEW = "pref_view";
	private static final String PREF_OWNER = "pref_owner";
	private static final String PREF_VIEWER = "pref_viewer";
	private static final String PREF_WIDTH = "pref_width";
	private static final String PREF_COUNTRY = "pref_country";
	private static final String PREF_LANG = "pref_lang";
	private static final String PREF_USE_EXTERNAL_BROWSER = "pref_use_external_browser";

	private List<Person> people;
	
	private String view;
	private String owner;
	private String viewer;
	private String width;
	private String country;
	private String language;
	private boolean useExternalBrowser;
	
	private Combo viewKind;
	private Combo owners;
	private Combo viewers;
	private Spinner widths;
	private Combo countries;
	private Combo languages;
	private Button useExternalBrowserCheck;
	
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
		Composite composite = (Composite)super.createDialogArea(parent);
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
				switch(viewKind.getSelectionIndex()) {
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
		Label separator = new Label(panel, SWT.SEPARATOR | SWT.HORIZONTAL);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 4;
		separator.setLayoutData(layoutData);
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
		setDefaultValues();
		//
		return composite;
	}

	private void setDefaultValues() {
		try {
			OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
			String prevCountry = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_COUNTRY));
			if (StringUtils.isNumeric(prevCountry)) {
				countries.select(Integer.parseInt(prevCountry));
			} else {
				for (int i = 0; i < countries.getItemCount(); i++) {
					String country = countries.getItem(i);
					if (country.substring(country.indexOf('(') + 1, country.length() - 1).equals(config.getDefaultCountry())) {
						countries.select(i);
						break;
					}
				}
			}
			String prevLang = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_LANG));
			if (StringUtils.isNumeric(prevLang)) {
				languages.select(Integer.parseInt(prevLang));
			} else {
				for (int i = 0; i < languages.getItemCount(); i++) {
					String language = languages.getItem(i);
					if (language.substring(language.indexOf('(') + 1, language.length() - 1).equals(config.getDefaultLanguage())) {
						languages.select(i);
						break;
					}
				}
			}
			String prevOwner = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_OWNER));
			if (StringUtils.isNumeric(prevOwner)) {
				owners.select(Integer.parseInt(prevOwner));
			}
			String prevViewer = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEWER));
			if (StringUtils.isNumeric(prevViewer)) {
				viewers.select(Integer.parseInt(prevViewer));
			}
			String prevUseExternalBrowser = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_USE_EXTERNAL_BROWSER));
			if (StringUtils.isNotEmpty(prevUseExternalBrowser)) {
				useExternalBrowserCheck.setSelection(Boolean.parseBoolean(prevUseExternalBrowser));
			}
			String prevView = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEW));
			if (StringUtils.isNumeric(prevView)) {
				viewKind.select(Integer.parseInt(prevView));
			}
			String prevWidth = gadgetXmlFile.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_WIDTH));
			if (StringUtils.isNumeric(prevWidth)) {
				widths.setSelection(Integer.parseInt(prevWidth));
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(550, 400);
	}

	@Override
	protected void okPressed() {
		view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
		viewer = viewers.getItem(viewers.getSelectionIndex());
		owner = owners.getItem(owners.getSelectionIndex());
		width = widths.getText();
		useExternalBrowser = useExternalBrowserCheck.getSelection();
		country = countries.getText();
		country = country.substring(country.indexOf('(') + 1, country.length() - 1);
		language = languages.getText();
		language = language.substring(language.indexOf('(') + 1, language.length() - 1);
		try {
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_COUNTRY), String.valueOf(countries.getSelectionIndex()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_LANG), String.valueOf(languages.getSelectionIndex()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_OWNER), String.valueOf(owners.getSelectionIndex()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEWER), String.valueOf(viewers.getSelectionIndex()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_VIEW), String.valueOf(viewKind.getSelectionIndex()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_WIDTH), String.valueOf(widths.getSelection()));
			gadgetXmlFile.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, PREF_USE_EXTERNAL_BROWSER), String.valueOf(useExternalBrowserCheck.getSelection()));
		} catch (CoreException e) {
			e.printStackTrace();
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

}
