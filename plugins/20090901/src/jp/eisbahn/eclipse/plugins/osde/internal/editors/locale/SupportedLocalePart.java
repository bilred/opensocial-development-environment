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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
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

import com.google.gadgets.model.Module;
import com.google.gadgets.model.MessageBundle.Msg;
import com.google.gadgets.model.Module.ModulePrefs;
import com.google.gadgets.model.Module.ModulePrefs.Locale;

/**
 * Defines the table for displaying supported locales of the gadget
 * 
 * @author Yoichiro Tanaka
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class SupportedLocalePart extends SectionPart implements IPartSelectionListener {
	
	private LocalePage page;
	
	private TableViewer supportedLocaleTableViewer;
	
	public SupportedLocalePart(Composite parent, IManagedForm managedForm, LocalePage page) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		this.page = page;
		createContents(getSection(), managedForm.getToolkit());
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("Supported Locales");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, false));
		
		// Table View of Supported Locales
		// Create the table
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		
		// Create table columns, the first column is dummy
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Language");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Country");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Inlined?");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText("Language Direction");
		column.setWidth(150);
		
		// Populate the table with supported locales
		supportedLocaleTableViewer = new TableViewer(table);
		supportedLocaleTableViewer.setContentProvider(new ArrayContentProvider());
		supportedLocaleTableViewer.setLabelProvider(new SupportedLocaleListLabelProvider());
		final SectionPart part = new SectionPart(section);
		supportedLocaleTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
		
		// Create buttons for adding and deleting supported locale
		// Create a pane to put buttons
		Composite buttonPane = toolkit.createComposite(composite);
		buttonPane.setLayout(new GridLayout());
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonPane.setLayoutData(layoutData);
		
		// Create add new locale button
		Button addLocaleButton = toolkit.createButton(buttonPane, "Add New Locale", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addLocaleButton.setLayoutData(layoutData);
		addLocaleButton.addSelectionListener(new AddButtonSelectionListener());
		
		// Create deleting button
		Button deleteButton = toolkit.createButton(buttonPane, "Delete", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		deleteButton.setLayoutData(layoutData);
		deleteButton.addSelectionListener(new DeleteButtonSelectionListener());
		
		// Create add new message button
		Button addMessageButton = toolkit.createButton(buttonPane, "Add New Message", SWT.PUSH);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = GridData.BEGINNING;
		addMessageButton.setLayoutData(layoutData);
		addMessageButton.addSelectionListener(new AddMessageButtonSelectionListener());
		
		// All UIs done, now set the input of the table
		supportedLocaleTableViewer.setInput(page.getModule().getModulePrefs().getLocales());

		section.setClient(composite);
	}
	
	/**
	 * This method is called by LocalePage to notify SupportedLocalePart
	 * that the module has been changed. SupportedLocalePart then writes new 
	 * message bundle files according the new module and refresh its display.
	 */
	public void refreshModule() {
		updateMessageBundleFiles();
		supportedLocaleTableViewer.setInput(page.getModule().getModulePrefs().getLocales());
		supportedLocaleTableViewer.refresh();
	}
	
	private IProject getProject() {
		IFile file = (IFile) page.getEditorInput().getAdapter(IResource.class);
		return file.getProject();
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		supportedLocaleTableViewer.refresh((Locale)((IStructuredSelection)selection).getFirstElement());
	}
	

	/**
	 * Outputs all message bundles to files
	 * 
	 * This method is called as a result of user interaction with Add or Remove button.
	 * More specifically, once the markDirty() is called. This method will be invoked.
	 * 
	 * TODO: use writeMessageBundleFile() in GadgetXmlSerializer.java
	 */
	public void updateMessageBundleFiles() {
		List<Locale> supportedLocales = (List<Locale>) page.getModule().getModulePrefs().getLocales();
		IProject project = getProject();
		removeAllMessageBundleFiles(project);
		removeAllLocalesFromModule();
		ModulePrefs modulePrefs = page.getModule().getModulePrefs();
		for (Locale locale : supportedLocales) {
			try {
				if (!locale.isInlined()) {
					String fileName = locale.getLang() + "_" + locale.getCountry() + ".xml";
					IFile bundleFile = project.getFile(fileName);
					if (bundleFile.exists()) {
						bundleFile.delete(true, new NullProgressMonitor());
					}
					
					ByteArrayInputStream in = new ByteArrayInputStream(locale.getMessageBundle().toString().getBytes("UTF-8"));
					bundleFile.create(in, true, new NullProgressMonitor());
					
					// TODO: messages URI should actually be a location on the web
					// This will cause problems once the gadget is submitted to production environment
					locale.setMessages(fileName);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			modulePrefs.addRequireOrOptionalOrPreload(locale);
		}
	}
	
	// TODO: Change this ugly logic, which requires refactoring of ModulePrefs
	public void removeAllLocalesFromModule() {
		List<Object> elements = page.getModule().getModulePrefs().getRequireOrOptionalOrPreload();
		List<Object> tmp = new ArrayList<Object>();
		for (Object element : elements) {
			if (!(element instanceof Locale)) {
				tmp.add(element);
			}
		}
		elements.clear();
		for (Object element : tmp) {
			elements.add(element);
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
						// TODO: should be more strict when searching message bundle files
						if (!name.startsWith("gadget") && name.endsWith(".xml")) {
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
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Adds a new locale to supported locales
	 * 
	 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
	 *
	 */
	private class AddButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			AddLocaleDialog dialog = new AddLocaleDialog(page.getSite().getShell());
			if (dialog.open() == AddLocaleDialog.OK) {
				Locale newLocale = new Locale();
				String lang = dialog.getSelectedLanguage();
				String country = dialog.getSelectedCountry();
				String languageDirection = dialog.getLanguageDirection();
				boolean inlined = dialog.getIsInlined();
				
				newLocale.setLang(lang);
				newLocale.setCountry(country);
				newLocale.setLanguageDirection(languageDirection);
				newLocale.setInlined(inlined);
				
				if (!inlined) {
					String filePath = getProject().getLocation().toString();
					newLocale.setMessages(filePath + File.separator + lang + "_" + country + ".xml");
				}
				
				// Add the new Locale into Module
				List<Locale> currentSupportedLocales = (ArrayList<Locale>) page.getModule().getModulePrefs().getLocales();
				if (!currentSupportedLocales.contains(newLocale)) {
					page.getModule().getModulePrefs().addRequireOrOptionalOrPreload(newLocale);
					markDirty();
				}
			}
		}
	}
	
	private class DeleteButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedLocaleTableViewer.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection) selection;
				final Locale selectedLocale = (Locale) structured.getFirstElement();
				String lang = selectedLocale.getLang();
				lang = StringUtils.isEmpty(lang) ? "ALL" : lang;
				String country = selectedLocale.getCountry();
				country = StringUtils.isEmpty(country) ? "ALL" : country;
				if (MessageDialog.openConfirm(page.getSite().getShell(),
						"Deleting Locale", "Do you want to delete locale '" + lang + "_" + country + "'?")) {

					// TODO: change this ugly logic by refactoring Module.java
					List<?> modulePrefs = page.getModule().getModulePrefs().getRequireOrOptionalOrPreload();
					for (Object element : modulePrefs) {
						if (element instanceof Locale) {
							if (((Locale)element).equals(selectedLocale)) {
								modulePrefs.remove(element);
								break;
							}
						}
					}
					markDirty();
				}
			}
		}
	}
	
	private class AddMessageButtonSelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			ISelection selection = supportedLocaleTableViewer.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structured = (IStructuredSelection) selection;
				final Locale selectedLocale = (Locale) structured.getFirstElement();
				AddMessageDialog dialog = new AddMessageDialog(page.getSite().getShell(), selectedLocale);
				if (dialog.open() == AddMessageDialog.OK) {
					String messageName = dialog.getMessageName();
					String messageContent = dialog.getMessageContent();
					String messageDesc = dialog.getDescription();
					
					// TODO: ugly logic
					Module module = page.getModule();
					for (Object element : module.getModulePrefs().getRequireOrOptionalOrPreload()) {
						if (element instanceof Locale) {
							Locale locale = (Locale)element;
							if (locale.equals(selectedLocale)) {
								Msg msg = new Msg(messageName, messageContent, messageDesc);
								if (locale.isInlined()) {
									locale.addInlineMessage(msg);
								} else {
									locale.getMessageBundle().addMessage(msg);
								}
							}
						}
					}
				}
				markDirty();
				supportedLocaleTableViewer.setSelection(selection);
			}
		}
	}
}
