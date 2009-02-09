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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.activities;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ActivityService;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

public class ActivitiesPart extends SectionPart implements IPartSelectionListener {
	
	private ActivitiesView activitiesView;
	private Combo peopleCombo;
	private TableViewer activityList;

	public ActivitiesPart(Composite parent, IManagedForm managedForm, ActivitiesView activitiesView) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		createContents(getSection(), managedForm.getToolkit());
		this.activitiesView = activitiesView;
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("Activities ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(3, false));
		section.setClient(composite);
		//
		toolkit.createLabel(composite, "Person:");
		peopleCombo = new Combo(composite, SWT.READ_ONLY);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		peopleCombo.setLayoutData(layoutData);
		peopleCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateActivityList();
			}
		});
		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		Image image = imageRegistry.getDescriptor("icons/action_refresh.gif").createImage();
		ImageHyperlink reloadLink = toolkit.createImageHyperlink(composite, SWT.TOP);
		reloadLink.setImage(image);
		reloadLink.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				updateActivityList();
			}
			public void linkEntered(HyperlinkEvent e) {
			}
			public void linkExited(HyperlinkEvent e) {
			}
		});
		//
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 3;
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Title");
		column.setWidth(160);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Application");
		column.setWidth(150);
		activityList = new TableViewer(table);
		activityList.setContentProvider(new ActivityListContentProvider());
		activityList.setLabelProvider(new ActivityListLabelProvider());
		final SectionPart part = new SectionPart(section);
		activityList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
	}

	private void updateActivityList() {
		try {
			ActivityService activityService = Activator.getDefault().getActivityService();
			int index = peopleCombo.getSelectionIndex();
			if (index != -1) {
				Person person = (Person)peopleCombo.getData(peopleCombo.getItem(index));
				List<Activity> activities = (List<Activity>)activityService.getActivities(person);
				activityList.setInput(activities);
			}
		} catch (ConnectionException e) {
			MessageDialog.openError(activitiesView.getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}

	public void setPeople(List<Person> people) {
		peopleCombo.removeAll();
		if (!people.isEmpty()) {
			for (Person person : people) {
				peopleCombo.add(person.getId());
				peopleCombo.setData(person.getId(), person);
			}
			peopleCombo.select(0);
			updateActivityList();
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
	}

}
