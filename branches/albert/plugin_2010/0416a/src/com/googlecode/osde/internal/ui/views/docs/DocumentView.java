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
package com.googlecode.osde.internal.ui.views.docs;

import java.util.LinkedHashMap;
import java.util.Map;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.ui.views.AbstractView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DocumentView extends AbstractView {

    public static final String ID = "com.googlecode.osde.internal.views.DocumentView";

    private Browser browser;
    private Combo sitesCombo;

    private Action homeAction;

    private Map<String, String> siteMap;

    private TableViewer siteListTable;

    @Override
    protected void createForm(Composite parent) {
        CTabFolder tabFolder = new CTabFolder(parent, SWT.NONE);
        tabFolder.setTabPosition(SWT.BOTTOM);
        //
        CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("Browser");
        //
        Composite composite = new Composite(tabFolder, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        //
        sitesCombo = new Combo(composite, SWT.READ_ONLY);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        sitesCombo.setLayoutData(layoutData);
        sitesCombo.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                changeUrl();
            }
        });
        browser = new Browser(composite, SWT.NONE);
        browser.addPaintListener(new PaintListener() {
            private boolean flg = true;

            public void paintControl(PaintEvent e) {
                if (flg) {
                    getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            browser.setVisible(false);
                            browser.setVisible(true);
                            flg = false;
                        }
                    });
                }
            }
        });
        layoutData = new GridData(GridData.FILL_BOTH);
        browser.setLayoutData(layoutData);
        tabItem.setControl(composite);
        //
        tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("Site settings");
        //
        composite = new Composite(tabFolder, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        tabItem.setControl(composite);
        //
        Table table = new Table(composite,
                SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        layoutData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(layoutData);
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("");
        column.setWidth(20);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Site name");
        column.setWidth(200);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("URL");
        column.setWidth(400);
        siteListTable = new TableViewer(table);
        siteListTable.setContentProvider(new SiteListContentProvider());
        siteListTable.setLabelProvider(new SiteListLabelProvider());
        //
        composite = new Composite(composite, SWT.NONE);
        composite.setLayout(new GridLayout());
        layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        composite.setLayoutData(layoutData);
        Button addSiteButton = new Button(composite, SWT.PUSH);
        addSiteButton.setText("Create");
        addSiteButton.addSelectionListener(new AddSiteButtonSelectionListenerImpl());
        Button delSiteButton = new Button(composite, SWT.PUSH);
        delSiteButton.setText("Delete");
        delSiteButton.addSelectionListener(new DelSiteButtonSelectionListenerImpl());
        Button defaultSiteButton = new Button(composite, SWT.PUSH);
        defaultSiteButton.setText("Default");
        defaultSiteButton.addSelectionListener(new DefaultSiteButtonSelectionListenerImpl());
        //
        loadSites();
        tabFolder.setSelection(0);
    }

    private void loadSites() {
        OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
        Map<String, String> map = config.getDocsSiteMap();
        if (map != null) {
            siteMap = map;
        } else {
            siteMap = new LinkedHashMap<String, String>();
            setDefaultSites(siteMap);
            Activator.getDefault().storePreference(OsdeConfig.DOCS_SITE_MAP, siteMap);
        }
        siteListTable.setInput(siteMap);
        //
        sitesCombo.removeAll();
        for (Map.Entry<String, String> entry : siteMap.entrySet()) {
            sitesCombo.add(entry.getKey());
            sitesCombo.setData(entry.getKey(), entry.getValue());
        }
        sitesCombo.select(0);
        changeUrl();
    }

    @Override
    public void setFocus() {
    }

    @Override
    protected void fillContextMenu(IMenuManager manager) {
        super.fillContextMenu(manager);
        manager.add(homeAction);
    }

    @Override
    protected void fillLocalPullDown(IMenuManager manager) {
        super.fillLocalPullDown(manager);
        manager.add(homeAction);
    }

    @Override
    protected void fillLocalToolBar(IToolBarManager manager) {
        super.fillLocalToolBar(manager);
        manager.add(homeAction);
    }

    @Override
    protected void makeActions() {
        super.makeActions();
        homeAction = new Action() {
            @Override
            public void run() {
                changeUrl();
            }
        };
        homeAction.setText("Home");
        homeAction.setToolTipText("Go to home.");
        homeAction.setImageDescriptor(
                Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_home.gif"));
    }

    private void changeUrl() {
        browser.setText("<html><head></head><body></body></html>");
        int idx = sitesCombo.getSelectionIndex();
        if (idx != -1) {
            String url = (String) sitesCombo.getData(sitesCombo.getItem(idx));
            if (url != null && url.length() > 0) {
                browser.setUrl(url);
            }
        }
    }

    private class AddSiteButtonSelectionListenerImpl implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            AddSiteDialog dialog = new AddSiteDialog(getSite().getShell());
            if (dialog.open() == Window.OK) {
                String name = dialog.getName();
                String url = dialog.getUrl();
                siteMap.put(name, url);
                Activator.getDefault().storePreference(OsdeConfig.DOCS_SITE_MAP, siteMap);
                loadSites();
            }
        }

    }

    private class DelSiteButtonSelectionListenerImpl implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            ISelection selection = siteListTable.getSelection();
            if (!selection.isEmpty()) {
                IStructuredSelection structured = (IStructuredSelection) selection;

                @SuppressWarnings("unchecked")
                Map.Entry<String, String> entry =
                        (Map.Entry<String, String>) structured.getFirstElement();
                if (MessageDialog.openConfirm(getSite().getShell(), "Confirm",
                        "Would you like to delete the site '" + entry.getKey() + "'?")) {
                    siteMap.remove(entry.getKey());
                    Activator.getDefault().storePreference(OsdeConfig.DOCS_SITE_MAP, siteMap);
                    loadSites();
                }
            }
        }

    }

    private class DefaultSiteButtonSelectionListenerImpl implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            if (MessageDialog.openConfirm(getSite().getShell(), "Confirm",
                    "Would you like to add default sites?")) {
                setDefaultSites(siteMap);
                Activator.getDefault().storePreference(OsdeConfig.DOCS_SITE_MAP, siteMap);
                loadSites();
            }
        }

    }

    private void setDefaultSites(Map<String, String> siteMap) {
        siteMap.put("OpenSocial API Reference (0.8.1)",
                "http://www.opensocial.org/Technical-Resources/opensocial-spec-v081/opensocial-reference");
        siteMap.put("Gadgets API Reference (v0.8)",
                "http://www.opensocial.org/Technical-Resources/opensocial-spec-v08/gadgets-reference08");
        siteMap.put("OpenSocial API Blog", "http://blog.opensocial.org/");
        siteMap.put("orkut Developer Blog", "http://orkutdeveloper.blogspot.com/");
        siteMap.put("Blog - MySpace Developer Platform",
                "http://developer.myspace.com/community/blogs/");
        siteMap.put("Blog - hi5 developer platform",
                "http://www.hi5networks.com/developer/newsblog.html");
        siteMap.put("iGoogle Developer Blog", "http://igoogledeveloper.blogspot.com/");
        siteMap.put("OpenSocial - Google Code", "http://code.google.com/apis/opensocial/");
        siteMap.put("Orkut Developer Home", "http://code.google.com/apis/orkut/");
        siteMap.put("MySpace Developer Platform", "http://developer.myspace.com/community/");
        siteMap.put("hi5 developer platform", "http://www.hi5networks.com/developer/");
        siteMap.put("iGoogle Developer Home", "http://code.google.com/apis/igoogle/");
        siteMap.put("OpenSocial Java Client Library - Google code",
                "http://code.google.com/p/opensocial-java-client/");
        siteMap.put("OpenSocial Development Environment - Google Code",
                "http://code.google.com/p/opensocial-development-environment/");
    }

}
