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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.appdata;

import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.AppDataService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ShindigLauncher;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

public class AppDataView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.AppDataView";
	private Combo personCombo;
	private Combo applicationCombo;
	private List keyList;
	private Text valueText;
	private Action reloadAction;
	private Action removeAllAction;
	private Button addButton;
	private Button deleteButton;
	
	public AppDataView() {
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(reloadAction);
		manager.add(removeAllAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(reloadAction);
		manager.add(removeAllAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(reloadAction);
		manager.add(removeAllAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		reloadAction = new Action() {
			@Override
			public void run() {
				loadPeopleAndApplications();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload people and applications.");
		reloadAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/action_refresh.gif"));
		removeAllAction = new Action() {
			@Override
			public void run() {
				removeAllAppData();
			}
		};
		removeAllAction.setText("Remove all");
		removeAllAction.setToolTipText("Remove all application data.");
		removeAllAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/16-em-cross.gif"));
	}

	protected void createForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Form form = toolkit.createForm(parent);
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(layoutData);
		section.setText("AppData");
		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		section.setClient(composite);
		//
		Composite selectionPanel = toolkit.createComposite(composite);
		layout = new GridLayout();
		layout.numColumns = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		selectionPanel.setLayout(layout);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		selectionPanel.setLayoutData(layoutData);
		toolkit.createLabel(selectionPanel, "Person:");
		personCombo = new Combo(selectionPanel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		personCombo.setLayoutData(layoutData);
		personCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateDataMap();
			}
		});
		toolkit.createLabel(selectionPanel, "Application:");
		applicationCombo = new Combo(selectionPanel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		applicationCombo.setLayoutData(layoutData);
		applicationCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateDataMap();
			}
		});
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		Image image = imageRegistry.getDescriptor("icons/action_refresh.gif").createImage();
		ImageHyperlink reloadLink = toolkit.createImageHyperlink(selectionPanel, SWT.TOP);
		reloadLink.setImage(image);
		reloadLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				int index = keyList.getSelectionIndex();
				updateDataMap();
				keyList.select(index);
				updateValue();
			}
			public void linkEntered(HyperlinkEvent e) {
			}
			public void linkExited(HyperlinkEvent e) {
			}
		});
		//
		SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
		layoutData = new GridData(GridData.FILL_BOTH);
		sashForm.setLayoutData(layoutData);
		sashForm.setSashWidth(5);
		keyList = new List(sashForm, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		keyList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateValue();
			}
		});
		valueText = new Text(sashForm, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		valueText.setEditable(false);
		valueText.setBackground(toolkit.getColors().getBackground());
		sashForm.setWeights(new int[] {40, 60});
		//
		Composite buttonPane = toolkit.createComposite(composite);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		addButton.addSelectionListener(new AddButtonSelectionListener());
		deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
	}
	
	public void setFocus() {
	}
	
	private void updateValue() {
		String value = (String)keyList.getData(keyList.getItem(keyList.getSelectionIndex()));
		valueText.setText(value);
	}
	
	private void updateDataMap() {
		if (Activator.getDefault().isRunningShindig()) {
			try {
				Person person = (Person)personCombo.getData(personCombo.getText());
				ApplicationImpl application = (ApplicationImpl)applicationCombo.getData(applicationCombo.getText());
				AppDataService appDataService = Activator.getDefault().getAppDataService();
				Map<String, String> dataMap = appDataService.getApplicationDataMap(person, application);
				keyList.removeAll();
				valueText.setText("");
				if (dataMap != null) {
					for (Map.Entry<String, String> entry : dataMap.entrySet()) {
						keyList.add(entry.getKey());
						keyList.setData(entry.getKey(), entry.getValue());
					}
				}
			} catch(ConnectionException e) {
				MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
			}
		} else {
			ShindigLauncher.launchWithConfirm(getSite().getShell(), this);
		}
	}
	
	private void loadPeopleAndApplications() {
		if (Activator.getDefault().isRunningShindig()) {
			try {
				PersonService personService = Activator.getDefault().getPersonService();
				java.util.List<Person> people = personService.getPeople();
				personCombo.removeAll();
				for (Person person : people) {
					personCombo.add(person.getId());
					personCombo.setData(person.getId(), person);
				}
				personCombo.select(0);
				ApplicationService applicationService = Activator.getDefault().getApplicationService();
				java.util.List<ApplicationImpl> applications = applicationService.getApplications();
				applicationCombo.removeAll();
				for (ApplicationImpl application : applications) {
					applicationCombo.add(application.getTitle());
					applicationCombo.setData(application.getTitle(), application);
				}
				applicationCombo.select(0);
				updateDataMap();
			} catch(ConnectionException e) {
				MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
			}
		} else {
			ShindigLauncher.launchWithConfirm(getSite().getShell(), this);
		}
	}

	public void connectedDatabase() {
		loadPeopleAndApplications();
	}

	public void disconnectedDatabase() {
		personCombo.removeAll();
		applicationCombo.removeAll();
		keyList.removeAll();
		valueText.setText("");
	}
	
	public void removeAllAppData() {
		try {
			AppDataService appDataService = Activator.getDefault().getAppDataService();
			if (MessageDialog.openConfirm(getSite().getShell(), "Confirm", "Would you like to delete all application data?")) {
				appDataService.removeAll();
				loadPeopleAndApplications();
			}
		} catch (ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent evt) {
			if (Activator.getDefault().isRunningShindig()) {
				try {
					Person person = (Person)personCombo.getData(personCombo.getText());
					ApplicationImpl application = (ApplicationImpl)applicationCombo.getData(applicationCombo.getText());
					String key = keyList.getItem(keyList.getSelectionIndex());
					if (MessageDialog.openConfirm(getSite().getShell(), "Confirm", "Would you like to delete the [" + key + "] value?")) {
						AppDataService appDataService = Activator.getDefault().getAppDataService();
						appDataService.removeApplicationData(person, application, key);
						updateDataMap();
					}
				} catch(ConnectionException e) {
					MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
				}
			}
		}
	}

	private class AddButtonSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent evt) {
			if (Activator.getDefault().isRunningShindig()) {
				Person person = (Person)personCombo.getData(personCombo.getText());
				ApplicationImpl application = (ApplicationImpl)applicationCombo.getData(applicationCombo.getText());
				if (person != null && application != null) {
					AddAppDataDialog dialog = new AddAppDataDialog(getSite().getShell());
					if (dialog.open() == AddAppDataDialog.OK) {
						try {
							String key = dialog.getKey();
							String value = dialog.getValue();
							AppDataService appDataService = Activator.getDefault().getAppDataService();
							appDataService.addApplicationData(person, application, key, value);
							updateDataMap();
						} catch(ConnectionException e) {
							MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
						}
					}
				}
			}
		}
	}
	
}