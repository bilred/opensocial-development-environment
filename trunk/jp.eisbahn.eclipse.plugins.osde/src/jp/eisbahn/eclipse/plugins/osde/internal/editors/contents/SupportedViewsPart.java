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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.contents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;
import com.google.gadgets.ViewType;
import com.google.gadgets.Module.Content;

public class SupportedViewsPart extends SectionPart implements IPartSelectionListener {
	
	private ContentsPage page;
	
	private TableViewer supportedViewList;
	
	private ObjectFactory objectFactory;

	public SupportedViewsPart(Composite parent, IManagedForm managedForm, ContentsPage page) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		this.page = page;
		createContents(getSection(), managedForm.getToolkit());
		displayInitialValue();
		objectFactory = new ObjectFactory();
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("Supported Views ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		// Supported views list
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("View");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Type");
		column.setWidth(100);
		supportedViewList = new TableViewer(table);
		supportedViewList.setContentProvider(new SupportedViewListContentProvider());
		supportedViewList.setLabelProvider(new SupportedViewListLabelProvider());
		final SectionPart part = new SectionPart(section);
		supportedViewList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
		// Buttons
		Composite buttonPane = toolkit.createComposite(composite);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		Button addButton = toolkit.createButton(buttonPane, "Add", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addButton.setLayoutData(layoutData);
		addButton.addSelectionListener(new AddButtonSelectionListener());
		Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
		Button upButton = toolkit.createButton(buttonPane, "Up", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		upButton.setLayoutData(layoutData);
		upButton.addSelectionListener(new UpButtonSelectionListener());
		Button downButton = toolkit.createButton(buttonPane, "Down", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		downButton.setLayoutData(layoutData);
		downButton.addSelectionListener(new DownButtonSelectionListener());
		//
		section.setClient(composite);
	}

	private void displayInitialValue() {
		List<ContentModel> models = new ArrayList<ContentModel>();
		Module module = getModule();
		List<Content> contents = module.getContent();
		for (Content content : contents) {
			ContentModel model = new ContentModel();
			model.setType(ViewType.parse(content.getType()));
			model.setHref(content.getHref());
			model.setBody(content.getValue());
			model.setView(content.getView());
			models.add(model);
		}
		supportedViewList.setInput(models);
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		supportedViewList.refresh((ContentModel)((IStructuredSelection)selection).getFirstElement());
	}
	
	private Module getModule() {
		return page.getModule();
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		if (!onSave) {
			return;
		} else {
			setValuesToModule();
		}
	}
	
	private void setValuesToModule() {
		Module module = getModule();
		List<ContentModel> models = (List<ContentModel>)supportedViewList.getInput();
		List<Content> contents = module.getContent();
		contents.clear();
		for (ContentModel model : models) {
			Content content = objectFactory.createModuleContent();
			content.setHref(model.getHref());
			content.setType(model.getType().name());
			content.setValue(model.getBody());
			content.setView(model.getView());
			contents.add(content);
		}
	}
	
	private class AddButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			AddContentDialog dialog = new AddContentDialog(page.getSite().getShell());
			if (dialog.open() == AddContentDialog.OK) {
				ContentModel model = new ContentModel();
				model.setView(dialog.getView());
				model.setType(dialog.getType());
				List<ContentModel> models = (List<ContentModel>)supportedViewList.getInput();
				models.add(model);
				supportedViewList.refresh();
				markDirty();
			}
		}
		
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedViewList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				final ContentModel model = (ContentModel)structured.getFirstElement();
				String view = model.getView();
				view = StringUtils.isEmpty(view) ? "(any)" : view;
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting content", "Do you want to delete the content '" + view + "'?")) {
					List<ContentModel> models = (List<ContentModel>)supportedViewList.getInput();
					models.remove(model);
					supportedViewList.refresh();
					markDirty();
				}
			}
		}
		
	}

	private class UpButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedViewList.getSelection();
			if (!selection.isEmpty()) {
				List<ContentModel> models = (List<ContentModel>)supportedViewList.getInput();
				IStructuredSelection structured = (IStructuredSelection)selection;
				ContentModel model = (ContentModel)structured.getFirstElement();
				int index = models.indexOf(model);
				if (index != 0) {
					Collections.swap(models, index, index - 1);
					supportedViewList.refresh();
					markDirty();
				}
			}
		}
		
	}

	private class DownButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedViewList.getSelection();
			if (!selection.isEmpty()) {
				List<ContentModel> models = (List<ContentModel>)supportedViewList.getInput();
				IStructuredSelection structured = (IStructuredSelection)selection;
				ContentModel model = (ContentModel)structured.getFirstElement();
				int index = models.indexOf(model);
				int size = models.size();
				if (index != (size - 1)) {
					Collections.swap(models, index, index + 1);
					supportedViewList.refresh();
					markDirty();
				}
			}
		}
		
	}

	public List<ContentModel> getContentModels() {
		return (List<ContentModel>)supportedViewList.getInput();
	}

}
