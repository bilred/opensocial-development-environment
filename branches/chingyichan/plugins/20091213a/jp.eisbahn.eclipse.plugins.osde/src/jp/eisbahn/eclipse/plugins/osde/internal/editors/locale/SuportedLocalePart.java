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

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
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
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.model.MessageBundle;
import com.google.gadgets.model.Module;
import com.google.gadgets.model.Module.ModulePrefs;
import com.google.gadgets.model.Module.ModulePrefs.Locale;
import com.google.gadgets.model.MessageBundle.Msg;

public class SuportedLocalePart extends SectionPart implements IPartSelectionListener {

    private static final Logger logger = new Logger(SuportedLocalePart.class);

    private LocalePage page;

    private TableViewer supportedLocaleList;

    public SuportedLocalePart(Composite parent, IManagedForm managedForm, LocalePage page) {
        super(parent, managedForm.getToolkit(), ExpandableComposite.TITLE_BAR);
        initialize(managedForm);
        this.page = page;
        createContents(getSection(), managedForm.getToolkit());
        displayInitialValue();
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
        if (module != null) {
            ModulePrefs modulePrefs = module.getModulePrefs();
            List<Locale> locales = modulePrefs.getLocales();
            for (Locale locale : locales) {
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

    @SuppressWarnings("unchecked")
    public void setValuesToModule() {
        Module module = getModule();
        ModulePrefs modulePrefs = module.getModulePrefs();
        List<Locale> locales = modulePrefs.getLocales();
        locales.clear();
        List<LocaleModel> models = (List<LocaleModel>) supportedLocaleList.getInput();
        IFile file = (IFile) page.getEditorInput().getAdapter(IResource.class);
        IProject project = file.getProject();
        removeAllMessageBundleFiles(project);
        for (LocaleModel model : models) {
            Locale locale = new Locale();
            String country = model.getCountry();
            if (!StringUtils.isEmpty(country)) {
                locale.setCountry(country);
            }
            String lang = model.getLang();
            if (!StringUtils.isEmpty(lang)) {
                locale.setLang(lang);
            }
            if (model.isInternal()) {
                List<Msg> msgs = locale.getInlineMessages();
                Map<String, String> messages = model.getMessages();
                for (Map.Entry<String, String> entry : messages.entrySet()) {
                    Msg msg = new Msg();
                    msg.setName(entry.getKey());
                    msg.setContent(entry.getValue());
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
                    MessageBundle msgBundle = new MessageBundle();
                    Map<String, String> messages = model.getMessages();
                    for (Map.Entry<String, String> entry : messages.entrySet()) {
                        msgBundle.addMessage(new Msg(entry.getKey(), entry.getValue()));
                    }

                    ByteArrayInputStream in = new ByteArrayInputStream(msgBundle.toString().getBytes("UTF-8"));
                    bundleFile.create(in, true, new NullProgressMonitor());

                    // TODO: what's the rule about the 8080 port?
                    locale.setMessages("http://localhost:8080/" + project.getName() + "/" + fileName);
                    locale.setMessageBundle(msgBundle);
                } catch (CoreException e) {
                    logger.warn("Creating the message bundle file failed.", e);
                } catch (UnsupportedEncodingException e) {
                    logger.warn("Creating the message bundle file failed.", e);
                }
            }
            locales.add(locale);
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
            // TODO: Handle CoreException.
            throw new IllegalStateException(e);
        }
    }

    private class AddButtonSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            AddLocaleDialog dialog = new AddLocaleDialog(page.getSite().getShell());
            if (dialog.open() == Window.OK) {
                LocaleModel model = new LocaleModel();
                model.setCountry(dialog.getCountry());
                model.setLang(dialog.getLanguage());
                model.setInternal(dialog.isInternal());
                List<LocaleModel> models = getLocaleModels();
                if (!contains(models, model)) {
                    models.add(model);
                    supportedLocaleList.refresh();
                    markDirty();
                }
            }
        }

        private boolean contains(List<LocaleModel> models, LocaleModel model) {
            for (LocaleModel localeModel : models) {
                if (equalsLocaleModel(localeModel, model)) {
                    return true;
                }
            }
            return false;
        }

        private boolean equalsLocaleModel(LocaleModel m1, LocaleModel m2) {
            return new EqualsBuilder()
                    .append(m1.getCountry(), m2.getCountry())
                    .append(m1.getLang(), m2.getLang())
                    .isEquals();
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
                    List<LocaleModel> models = getLocaleModels();
                    models.remove(model);
                    supportedLocaleList.refresh();
                    markDirty();
                }
            }
        }

    }


    @SuppressWarnings("unchecked")
    public List<LocaleModel> getLocaleModels() {
        return (List<LocaleModel>) supportedLocaleList.getInput();
    }

    public void changeModel() {
        displayInitialValue();
    }

}
