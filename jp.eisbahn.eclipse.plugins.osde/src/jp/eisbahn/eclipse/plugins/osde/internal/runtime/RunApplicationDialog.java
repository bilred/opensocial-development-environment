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

import org.apache.shindig.social.opensocial.model.Person;
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

	private List<Person> people;
	
	private String view;
	private String owner;
	private String viewer;
	private String width;
	
	private Combo viewKind;

	private Combo owners;

	private Combo viewers;

	private Spinner widths;

	private Button useExternalBrowserCheck;

	private boolean useExternalBrowser;
	
	public RunApplicationDialog(Shell shell, List<Person> people) {
		super(shell);
		this.people = people;
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
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		view = viewKind.getItem(viewKind.getSelectionIndex()).toLowerCase();
		viewer = viewers.getItem(viewers.getSelectionIndex());
		owner = owners.getItem(owners.getSelectionIndex());
		width = widths.getText();
		useExternalBrowser = useExternalBrowserCheck.getSelection();
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

}
