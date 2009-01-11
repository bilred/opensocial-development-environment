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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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

import antlr.MakeGrammar;

import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;
import com.google.gadgets.Module.ModulePrefs;
import com.google.gadgets.Module.ModulePrefs.Locale;
import com.google.gadgets.Module.ModulePrefs.Locale.Msg;

public class SuportedLocalePart extends SectionPart implements IPartSelectionListener {
	
	private LocalePage page;
	
	private TableViewer supportedLocaleList;
	
	private ObjectFactory objectFactory;

	public SuportedLocalePart(Composite parent, IManagedForm managedForm, LocalePage page) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		this.page = page;
		createContents(getSection(), managedForm.getToolkit());
		displayInitialValue();
		objectFactory = new ObjectFactory();
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("Supported Locales ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		// Supported locales list
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Country");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Language");
		column.setWidth(100);
		supportedLocaleList = new TableViewer(table);
		supportedLocaleList.setContentProvider(new SupportedLocaleListContentProvider());
		supportedLocaleList.setLabelProvider(new SupportedLocaleListLabelProvider());
		final SectionPart part = new SectionPart(section);
		supportedLocaleList.addSelectionChangedListener(new ISelectionChangedListener() {
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
		//
		section.setClient(composite);
	}

	private void displayInitialValue() {
		IProject project = getProject();
		List<LocaleModel> models = new ArrayList<LocaleModel>();
		Module module = getModule();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> list = modulePrefs.getRequireOrOptionalOrPreload();
		for (JAXBElement<?> element : list) {
			Object value = element.getValue();
			if (value instanceof Locale) {
				Locale locale = (Locale)value;
				LocaleModel model = new LocaleModel(locale, project);
				models.add(model);
			}
		}
		supportedLocaleList.setInput(models);
	}
	
	private IProject getProject() {
		IFile file = (IFile)page.getEditorInput().getAdapter(IResource.class);
		return file.getProject();
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		supportedLocaleList.refresh((LocaleModel)((IStructuredSelection)selection).getFirstElement());
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
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> elements = modulePrefs.getRequireOrOptionalOrPreload();
		elements = removeAllLocale(elements);
		List<LocaleModel> models = (List<LocaleModel>)supportedLocaleList.getInput();
		IFile file = (IFile)page.getEditorInput().getAdapter(IResource.class);
		IProject project = file.getProject();
		removeAllMessageBundleFiles(project);
		for (LocaleModel model : models) {
			Locale locale = objectFactory.createModuleModulePrefsLocale();
			locale.setCountry(model.getCountry());
			locale.setLang(model.getLang());
			List<Msg> msgs = locale.getMsg();
			if (model.isInternal()) {
				Map<String, String> messages = model.getMessages();
				for (Map.Entry<String, String> entry : messages.entrySet()) {
					Msg msg = objectFactory.createModuleModulePrefsLocaleMsg();
					msg.setName(entry.getKey());
					msg.setValue(entry.getValue());
					msgs.add(msg);
				}
			} else {
				try {
					String fileName = LocaleModel.MESSAGE_BUNDLE_FILENAME_PREFIX
							+ model.getLang() + "_" + model.getCountry() + ".xml";
					IFile bundleFile = project.getFile(fileName);
					if (bundleFile.exists()) {
							bundleFile.delete(true, new NullProgressMonitor());
					}
					StringBuilder sb = new StringBuilder();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<messagebundle>\n");
					Map<String, String> messages = model.getMessages();
					for (Map.Entry<String, String> entry : messages.entrySet()) {
						sb.append("  <msg name=\"" + entry.getKey() + "\">" + entry.getValue() + "</msg>\n");
					}
					sb.append("</messagebundle>");
					ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF8"));
					bundleFile.create(in, true, new NullProgressMonitor());
					int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
					locale.setMessages("http://localhost:" + port + "/" + fileName);
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			elements.add(objectFactory.createModuleModulePrefsLocale(locale));
		}
	}
	
	private void removeAllMessageBundleFiles(IProject project) {
		try {
			project.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					int type = resource.getType();
					switch(type) {
					case IResource.PROJECT:
						return true;
					case IResource.FILE:
						String name = resource.getName();
						if (name.startsWith("messages_") && name.endsWith(".xml")) {
							resource.delete(true, new NullProgressMonitor());
						}
						return false;
					case IResource.FOLDER:
						return false;
					default:
						return false;
					}
				}
			});
		} catch (CoreException e) {
			// TODO 
			throw new IllegalStateException(e);
		}
	}

	private List<JAXBElement<?>> removeAllLocale(List<JAXBElement<?>> elements) {
		List<JAXBElement<?>> targetList = new ArrayList<JAXBElement<?>>();
		for (JAXBElement<?> element : elements) {
			if (element.getValue() instanceof Locale) {
				targetList.add(element);
			}
		}
		elements.removeAll(targetList);
		return elements;
	}

	private class AddButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			AddLocaleDialog dialog = new AddLocaleDialog(page.getSite().getShell());
			if (dialog.open() == AddLocaleDialog.OK) {
				LocaleModel model = new LocaleModel();
				model.setCountry(dialog.getCountry());
				model.setLang(dialog.getLanguage());
				model.setInternal(dialog.isInternal());
				List<LocaleModel> models = (List<LocaleModel>)supportedLocaleList.getInput();
				if (!models.contains(model)) {
					models.add(model);
					supportedLocaleList.refresh();
					markDirty();
				}
			}
		}
		
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedLocaleList.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				final LocaleModel model = (LocaleModel)structured.getFirstElement();
				String country = model.getCountry();
				country = StringUtils.isEmpty(country) ? "(any)" : country;
				String lang = model.getLang();
				lang = StringUtils.isEmpty(lang) ? "(any)" : lang;
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting locale", "Do you want to delete locale '" + lang + "_" + country + "'?")) {
					List<LocaleModel> models = (List<LocaleModel>)supportedLocaleList.getInput();
					models.remove(model);
					supportedLocaleList.refresh();
					markDirty();
				}
			}
		}
		
	}

}
