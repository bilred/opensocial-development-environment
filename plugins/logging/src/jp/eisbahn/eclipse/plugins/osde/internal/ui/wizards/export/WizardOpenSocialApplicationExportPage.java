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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.export;

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createText;

import java.util.ArrayList;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.OsdeProjectNature;
import jp.eisbahn.eclipse.plugins.osde.internal.runtime.AbstractRunAction;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class WizardOpenSocialApplicationExportPage extends WizardPage {
    private static final Logger logger = new Logger(WizardOpenSocialApplicationExportPage.class);
	private TableViewer projectList;
	private Text urlText;
	private Text outputText;
	private String output;
	private String url;
	private IProject project;

	public WizardOpenSocialApplicationExportPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createControls(composite);
		//
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private void createControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		Label label = createLabel(composite, "Target project:");
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);
		Table table = new Table(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(25);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Name");
		column.setWidth(400);
		projectList = new TableViewer(table);
		projectList.setContentProvider(new ProjectListContentProvider());
		projectList.setLabelProvider(new ProjectListLabelProvider());
		projectList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				validate();
			}
		});
		//
		label = createLabel(composite, "URL to be deployed (replace 'http://localhost:8081/' with):");
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);
		urlText = createText(composite);
		urlText.setText("http://your.server.host/");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		urlText.setLayoutData(layoutData);
		urlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		//
		label = createLabel(composite, "Output:");
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);
		outputText = createText(composite);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		outputText.setLayoutData(layoutData);
		outputText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		Button referenceButton = new Button(composite, SWT.PUSH);
		referenceButton.setText("Browse...");
		referenceButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				String[] filterNames = new String[] { "All files(*.*)", "Zip files(*.zip)" };
				String[] filterExtensions = new String[] { "*.*", "*.zip" };
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setText("Output file");
//				dialog.setFilterPath("C:/");
//				dialog.setFileName("xxx.txt");
				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);
				String file = dialog.open();
				if (file != null) {
					outputText.setText(file);
				}
			}
		});
		//
		initialize();
	}

	private void initialize() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		List<IProject> openSocialProjects = new ArrayList<IProject>();
		for (IProject project : projects) {
			try {
				if (project.hasNature(OsdeProjectNature.ID)) {
					openSocialProjects.add(project);
				}
			} catch (CoreException e) {
				logger.error("Initializing the Export page failed.", e);
			}
		}
		projectList.setInput(openSocialProjects);
	}
	
	private void validate() {
		IStructuredSelection selection = (IStructuredSelection)projectList.getSelection();
		if (selection.isEmpty()) {
			setErrorMessage(null);
			setMessage("Target project is not selected.");
			setPageComplete(false);
			return;
		} else {
			project = (IProject)selection.getFirstElement();
		}
		url = urlText.getText();
		if (StringUtils.isEmpty(url)) {
			setErrorMessage(null);
			setMessage("URL is empty.");
			setPageComplete(false);
			return;
		}
		output = outputText.getText();
		if (StringUtils.isEmpty(output)) {
			setErrorMessage(null);
			setMessage("Output is empty.");
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		setMessage(null);
		setPageComplete(true);
	}
	
	public IProject getProject() {
		return project;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getOutput() {
		return output;
	}
	
}
