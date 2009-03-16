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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.docs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
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
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.DocumentView";
	
	private Browser browser;
	private Combo sitesCombo;
	
	private Action homeAction;
	
	private Map<String, String> siteMap;

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
		Table table = new Table(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
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
		TableViewer siteList = new TableViewer(table);
		siteList.setContentProvider(new SiteListContentProvider());
		siteList.setLabelProvider(new SiteListLabelProvider());
		//
		composite = new Composite(composite, SWT.NONE);
		composite.setLayout(new GridLayout());
		Button addSiteButton = new Button(composite, SWT.PUSH);
		addSiteButton.setText("Create");
		Button delSiteButton = new Button(composite, SWT.PUSH);
		delSiteButton.setText("Delete");
		//
		loadSites();
	}
	
	private void loadSites() {
		siteMap = new TreeMap<String, String>();
		siteMap.put("OpenSocial API Reference (0.8.1)", "http://www.opensocial.org/Technical-Resources/opensocial-spec-v081/opensocial-reference");
		siteMap.put("Gadgets API Reference (v0.8)", "http://www.opensocial.org/Technical-Resources/opensocial-spec-v08/gadgets-reference08");
		try {
			String encodeSiteMap = encodeSiteMap(siteMap);
			System.out.println(encodeSiteMap);
			Map<String, String> decodeSiteMap = decodeSiteMap(encodeSiteMap);
			System.out.println(decodeSiteMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		sitesCombo.removeAll();
		sitesCombo.add("OpenSocial API Reference (0.8.1)");
		sitesCombo.setData("OpenSocial API Reference (0.8.1)", "http://www.opensocial.org/Technical-Resources/opensocial-spec-v081/opensocial-reference");
		sitesCombo.add("Gadgets API Reference (v0.8)");
		sitesCombo.setData("Gadgets API Reference (v0.8)", "http://www.opensocial.org/Technical-Resources/opensocial-spec-v08/gadgets-reference08");
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
		int idx = sitesCombo.getSelectionIndex();
		String url = (String)sitesCombo.getData(sitesCombo.getItem(idx));
		browser.setUrl(url);
	}
	
	private String encodeSiteMap(Map<String, String> siteMap) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(siteMap);
		out.flush();
		byte[] bytes = baos.toByteArray();
		byte[] encoded = Base64.encodeBase64(bytes);
		return new String(encoded, "UTF-8");
	}
	
	private Map<String, String> decodeSiteMap(String encodeSiteMap) throws IOException, ClassNotFoundException {
		byte[] bytes = encodeSiteMap.getBytes("UTF-8");
		byte[] decoded = Base64.decodeBase64(bytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
		ObjectInputStream in = new ObjectInputStream(bais);
		return (Map<String, String>)in.readObject();
	}
	
}
