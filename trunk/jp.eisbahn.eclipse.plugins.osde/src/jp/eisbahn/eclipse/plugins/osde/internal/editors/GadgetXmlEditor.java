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
package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.basic.ModulePrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.contents.ContentsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.locale.LocalePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.outline.GadgetXmlOutlinePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefsPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import com.google.gadgets.Module;

public class GadgetXmlEditor extends FormEditor {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.editors.GadgetXmlEditor";
	
	private Module module;
	private JAXBContext context;

	private ModulePrefsPage modulePrefsPage;

	private ContentsPage contentsPage;

	private LocalePage messageBundlePage;

	private UserPrefsPage userPrefsPage;

	private StructuredTextEditor sourceEditor;
	
	private boolean reflecting = false;
	
	private GadgetXmlOutlinePage outlinePage;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		try {
			IFile file = (IFile)input.getAdapter(IResource.class);
			context = JAXBContext.newInstance(Module.class);
			Unmarshaller um = context.createUnmarshaller();
			module = (Module)um.unmarshal(file.getContents());
			setPartName(file.getName());
		} catch (JAXBException e) {
			throw new PartInitException(e.getErrorCode(), e);
		} catch (CoreException e) {
			throw new PartInitException(e.getMessage(), e);
		}
	}

	@Override
	protected void addPages() {
		try {
			modulePrefsPage = new ModulePrefsPage(this, module);
			addPage(modulePrefsPage);
			contentsPage = new ContentsPage(this, module);
			addPage(contentsPage);
			messageBundlePage = new LocalePage(this, module);
			addPage(messageBundlePage);
			userPrefsPage = new UserPrefsPage(this, module);
			addPage(userPrefsPage);
			sourceEditor = new StructuredTextEditor() {
				@Override
				public void doSave(IProgressMonitor progressMonitor) {
					try {
						reflectModel();
					} catch (JAXBException e) {
						// Ignore
					}
					commitPages(true);
					super.doSave(progressMonitor);
					editorDirtyStateChanged();
				}
			};
			addPage(sourceEditor, getEditorInput());
			setPageText(4, "Source");
			addPageChangedListener(new IPageChangedListener() {
				public void pageChanged(PageChangedEvent event) {
					if (sourceEditor.isDirty()) {
						try {
							reflectModel();
						} catch (JAXBException e) {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Syntax error: " + e.getCause().getMessage());
							setActiveEditor(sourceEditor);
						}
					}
				}
			});
			sourceEditor.getDocumentProvider().getDocument(getEditorInput()).addDocumentListener(new IDocumentListener() {
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
				public void documentChanged(DocumentEvent event) {
					outlinePage.refresh();
				}
			});
		} catch(PartInitException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void changeModel(Module model) {
		this.module = model;
		modulePrefsPage.changeModel(model);
		contentsPage.changeModel(model);
		messageBundlePage.changeModel(model);
		userPrefsPage.changeModel(model);
	}

	@Override
	public void editorDirtyStateChanged() {
		super.editorDirtyStateChanged();
		if (isDirty() && !reflecting) {
			modulePrefsPage.updateModel();
			contentsPage.updateModel();
			userPrefsPage.updateModel();
			messageBundlePage.updateModel();
			String serialized = GadgetXmlSerializer.serialize(module);
			IDocument document = sourceEditor.getDocumentProvider().getDocument(getEditorInput());
			String now = document.get();
			if (!serialized.equals(now)) {
				document.set(serialized);
			}
			outlinePage.refresh();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		commitPages(true);
		sourceEditor.doSave(monitor);
		editorDirtyStateChanged();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void reflectModel() throws JAXBException {
		reflecting = true;
		try {
			IDocument document = sourceEditor.getDocumentProvider().getDocument(getEditorInput());
			String content = document.get();
			Unmarshaller um = context.createUnmarshaller();
			StringReader in = new StringReader(content);
			changeModel((Module)um.unmarshal(in));
		} finally {
			reflecting = false;
		}
	}
	
	public String getSource() {
		IDocument document = sourceEditor.getDocumentProvider().getDocument(getEditorInput());
		String content = document.get();
		return content;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			if (outlinePage == null) {
				outlinePage = new GadgetXmlOutlinePage(this);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}
	
	public void activateSourceEditor(int lineNumber) {
		setActivePage(4);
		IDocument document = sourceEditor.getDocumentProvider().getDocument(getEditorInput());
		String content = document.get();
		char[] chars = content.toCharArray();
		int cnt = 0;
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\n') {
				cnt++;
				if (cnt == lineNumber) {
					pos = i;
					break;
				}
			}
		}
		sourceEditor.selectAndReveal(pos, 0);
	}

}
