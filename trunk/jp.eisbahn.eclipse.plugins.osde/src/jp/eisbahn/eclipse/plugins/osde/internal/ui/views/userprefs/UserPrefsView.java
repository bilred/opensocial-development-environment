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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs;

import java.util.List;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefModel;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefModel.DataType;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class UserPrefsView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.UserPrefsView";
	
	private Action reloadAction;

	private Composite userPrefFieldsComposite;

	public UserPrefsView() {
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		reloadAction = new Action() {
			@Override
			public void run() {
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload people and applications.");
		reloadAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/action_refresh.gif"));
	}

	protected void createForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(layoutData);
		section.setText("Settings");
		//
		userPrefFieldsComposite = toolkit.createComposite(section);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		userPrefFieldsComposite.setLayoutData(layoutData);
		layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.numColumns = 2;
		userPrefFieldsComposite.setLayout(layout);
		section.setClient(userPrefFieldsComposite);
	}
	
	public void setFocus() {
	}
	
	private void removeAllFields() {
		getSite().getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				Control[] children = userPrefFieldsComposite.getChildren();
				for (Control control : children) {
					control.dispose();
				}
			}
		});
	}
	
	private void setupFields(final List<UserPrefModel> userPrefModels) {
		getSite().getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				FormToolkit toolkit = new FormToolkit(getSite().getShell().getDisplay());
				for (UserPrefModel model : userPrefModels) {
					toolkit.createLabel(userPrefFieldsComposite, model.getDisplayName());
					DataType dataType = model.getDataType();
					Control control = null;
					GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
					if (dataType.equals(DataType.STRING)) {
						control = toolkit.createText(
								userPrefFieldsComposite, model.getDefaultValue(), SWT.BORDER);
					} else if (dataType.equals(DataType.BOOL)) {
						control = new Button(userPrefFieldsComposite, SWT.CHECK);
						control.setBackground(toolkit.getColors().getBackground());
					} else if (dataType.equals(DataType.ENUM)) {
						control = new Combo(userPrefFieldsComposite, SWT.READ_ONLY);
						Map<String, String> enumValueMap = model.getEnumValueMap();
						for (Map.Entry<String, String> entry : enumValueMap.entrySet()) {
							String value = entry.getKey();
							String displayValue = StringUtils.isEmpty(entry.getValue()) ? value : entry.getValue();
							((Combo)control).add(displayValue);
							((Combo)control).setData(displayValue, entry.getKey());
						}
						((Combo)control).select(0);
					} else if (dataType.equals(DataType.LIST)) {
						control = toolkit.createText(
								userPrefFieldsComposite, model.getDefaultValue(),
								SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
						layoutData.heightHint = 50;
					} else {
						control = toolkit.createLabel(userPrefFieldsComposite, "---");
					}
					control.setLayoutData(layoutData);
				}
				userPrefFieldsComposite.layout();
			}
		});
	}
	
	public void connectedDatabase() {
	}
	
	public void showUserPrefFields(
			final String view, final String viewer, final String owner, final String appId,
			final String country, final String language, final String url) {
		Job job = new Job("Fetch metadata") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Fetch metadata", 3);
				try {
					removeAllFields();
					monitor.worked(1);
					List<UserPrefModel> userPrefModels =
						MetaDataFetcher.fetch(view, viewer, owner, appId, country, language, url);
					monitor.worked(1);
					setupFields(userPrefModels);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), "Error", "Fetching metadata failed.\n" + e.getMessage());
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
}