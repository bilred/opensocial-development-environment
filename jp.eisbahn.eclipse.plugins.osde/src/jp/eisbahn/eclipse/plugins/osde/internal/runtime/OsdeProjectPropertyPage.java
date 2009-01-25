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

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.dialogs.PropertyPage;

public class OsdeProjectPropertyPage extends PropertyPage {

	private Spinner portSpinner;

	public OsdeProjectPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(layoutData);
		//
		createServerSettingsControls(composite);
		//
		return composite;
	}
	
	private void createServerSettingsControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group localGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		localGroup.setText("Local environment for testing");
		localGroup.setFont(parent.getFont());
		localGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 2;
		localGroup.setLayout(layout);
		localGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createLabel(localGroup, "Port:");
		portSpinner = new Spinner(localGroup, SWT.BORDER);
		portSpinner.setMinimum(1024);
		portSpinner.setMaximum(65535);
		//
		try {
			IProject project = (IProject)getElement().getAdapter(IProject.class);
			portSpinner.setSelection(ProjectPreferenceUtils.getLocalWebServerPort(project));
		} catch(CoreException e) {
			// TODO something
			throw new IllegalStateException(e);
		}
	}

	protected void performDefaults() {
		try {
			IProject project = (IProject)getElement().getAdapter(IProject.class);
			int port = ProjectPreferenceUtils.setDefaultLocalWebServerPort(project);
			portSpinner.setSelection(port);
		} catch(CoreException e) {
			// TODO something
			throw new IllegalStateException(e);
		}
	}
	
	public boolean performOk() {
		try {
			IProject project = (IProject)getElement().getAdapter(IProject.class);
			ProjectPreferenceUtils.setLocalWebServerPort(project, portSpinner.getSelection());
		} catch(CoreException e) {
			// TODO something
			throw new IllegalStateException(e);
		}
		return true;
	}

}