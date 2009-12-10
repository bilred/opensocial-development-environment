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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.basic.ModulePrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.contents.ContentsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.locale.LocalePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.outline.GadgetXmlOutlinePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

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

import com.google.gadgets.GadgetXmlSerializer;
import com.google.gadgets.model.Module;
import com.google.gadgets.parser.IParser;
import com.google.gadgets.parser.ParserException;
import com.google.gadgets.parser.ParserFactory;
import com.google.gadgets.parser.ParserType;

/**
 * Editor for a gadget spec XML file.
 */
public class GadgetXmlEditor extends FormEditor {
    private static final Logger logger = new Logger(GadgetXmlEditor.class);

	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.editors.GadgetXmlEditor";

	private Module module;

	private ModulePrefsPage modulePrefsPage;

	private ContentsPage contentsPage;

	private LocalePage messageBundlePage;

	private UserPrefsPage userPrefsPage;

	private StructuredTextEditor sourceEditor;

	private boolean reflecting = false;

	private GadgetXmlOutlinePage outlinePage;

	private boolean initializeFailed = false;


	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		try {
			IFile file = (IFile)input.getAdapter(IResource.class);
			setPartName(file.getName());
			IParser gadgetXMLParser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
			try {
				module = (Module) gadgetXMLParser.parse(file.getContents());
			} catch (ParserException e) {
				logger.error(e.getMessage());
			}
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
					} catch (ParserException e) {
						logger.warn("Reflecting to the model failed.", e);
						// Ignore
					} catch (UnsupportedEncodingException e) {
						logger.warn("Reflecting to the model failed.", e);
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
					IParser parser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
					try {
						parser.parse(new ByteArrayInputStream(getSource().getBytes("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						MessageDialog.openError(getSite().getShell(), "Error",
								"Encoding error in source editor: " + e.getMessage());
						setActiveEditor(sourceEditor);
						return;
					} catch (ParserException e) {
						MessageDialog.openError(getSite().getShell(), "Error",
								"Syntax error: " + e.getMessage());
						setActiveEditor(sourceEditor);
						return;
					}
					if (sourceEditor.isDirty()) {
						try {
							reflectModel();
						} catch (ParserException e) {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Parsing error: " + e.getMessage());
							setActiveEditor(sourceEditor);
						} catch (UnsupportedEncodingException e) {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Encoding error: " + e.getMessage());
							setActiveEditor(sourceEditor);
						}
					}
				}
			});
			sourceEditor.getDocumentProvider().getDocument(getEditorInput()).addDocumentListener(new IDocumentListener() {
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
				public void documentChanged(DocumentEvent event) {
					if (outlinePage != null) {
						outlinePage.refresh();
					}
				}
			});
			if (initializeFailed) {
				setActiveEditor(sourceEditor);
			}
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
			if (outlinePage != null) {
				outlinePage.refresh();
			}
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

	private void reflectModel() throws ParserException, UnsupportedEncodingException {
		reflecting = true;
		try {
			IParser parser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
			String content = getSource();

			changeModel((Module) parser.parse(new ByteArrayInputStream(content.getBytes("UTF-8"))));
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
		String content = getSource();
		Map<Integer, Integer> lineInfoMap = getLineInformation(content);
		Integer pos = lineInfoMap.get(lineNumber);
		if (pos == null) {
			pos = 0;
		}
		sourceEditor.selectAndReveal(pos, 0);
	}

	private Map<Integer, Integer> getLineInformation(String content) {
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		char[] chars = content.toCharArray();
		int lineStart = 0;
		int lineNumber = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\n') {
				lineNumber++;
				resultMap.put(lineNumber, lineStart);
				lineStart = i + 1;
			}
		}
		resultMap.put(lineNumber++, lineStart);
		return resultMap;
	}

}
