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

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class DocumentView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.DocumentView";
	
	private Browser browser;
	private Combo sitesCombo;
	
	private Action configAction;
	private Action homeAction;

	@Override
	protected void createForm(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
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
		//
		setSites();
	}
	
	private void setSites() {
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
		manager.add(configAction);
		manager.add(homeAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(configAction);
		manager.add(homeAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(configAction);
		manager.add(homeAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		configAction = new Action() {
			@Override
			public void run() {
//				config();
			}
		};
		configAction.setText("Setting");
		configAction.setToolTipText("Setting sites.");
		configAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/list_settings.gif"));
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

}
