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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.basic.ModulePrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.contents.ContentsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.locale.LocalePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.outline.GadgetXmlOutlinePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
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
import com.google.gadgets.MessageBundle;
import com.google.gadgets.Module;
import com.google.gadgets.Module.ModulePrefs.Locale;
import com.google.gadgets.parser.IParser;
import com.google.gadgets.parser.ParserFactory;
import com.google.gadgets.parser.ParserType;

public class GadgetXmlEditor extends FormEditor {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.editors.GadgetXmlEditor";
	
	private Module module;

	private ModulePrefsPage modulePrefsPage;

	private ContentsPage contentsPage;

	private LocalePage localePage;

	private UserPrefsPage userPrefsPage;

	private StructuredTextEditor sourceEditor;
	
	private boolean refreshing = false;
	
	private GadgetXmlOutlinePage outlinePage;
	
	private boolean initializeFailed = false;
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		try {
			IFile file = (IFile) input.getAdapter(IResource.class);
			setPartName(file.getName());
			IParser gadgetXMLParser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
			IParser messageBundleXMLParser = ParserFactory.createParser(ParserType.MESSAGE_BUNDLE_XML_PARSER);
			module = (Module)gadgetXMLParser.parse(file.getContents());
			
			// For each locale, parse its message bundle file
			String projectPath = file.getLocation().toString();
			projectPath = projectPath.substring(0, projectPath.lastIndexOf("/"));
			for (Locale locale : module.getModulePrefs().getLocales()) {
				String fileName = projectPath + File.separator + locale.getLang() + "_" + locale.getCountry() + ".xml";
				File messageBundleFile = new File(fileName);
				if (!messageBundleFile.exists()) {
					messageBundleFile.createNewFile();
					MessageBundle msgBundle = new MessageBundle();
					FileWriter fout = new FileWriter(messageBundleFile);
					fout.write(msgBundle.toString());
					fout.flush();
					fout.close();
					locale.setMessageBundle(msgBundle);
				} else {
					MessageBundle parsedMessageBundle = (MessageBundle)messageBundleXMLParser.parse(messageBundleFile);
					locale.setMessageBundle(parsedMessageBundle);
				}
			}
		} catch (IOException e) {
			throw new PartInitException(e.getMessage(), e);
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
			localePage = new LocalePage(this, module);
			addPage(localePage);
			userPrefsPage = new UserPrefsPage(this, module);
			addPage(userPrefsPage);
			
			// Text editor for gadget.xml source codes
			sourceEditor = new StructuredTextEditor();
			addPage(sourceEditor, getEditorInput());
			setPageText(4, "Source Codes");
			
			/**
			 * If the source editor is edited and the user switches the page,
			 * parses the source codes and then updates the Module for all pages.
			 * Also refreshes the display of all pages by calling refreshModule()
			 */
			addPageChangedListener(new IPageChangedListener() {
				public void pageChanged(PageChangedEvent event) {
					if (sourceEditor.isDirty()) {
						parseSourceCodesAndRefreshPages();
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
	
	/**
	 * Retrieve the source codes from source editor and parse it.
	 * If the source codes are successfully parsed, refresh the module and all pages
	 * by calling refreshModule().
	 */
	protected void parseSourceCodesAndRefreshPages() {
		try {
			String contents = getSourceEditorContents();
			IParser parser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
			Logging.info("Got reference for parser.");
			module = (Module)parser.parse(new ByteArrayInputStream(contents.getBytes("UTF-8")));
			Logging.info("Done parsing gadget XML file.");
			refreshModule(module); // notifies all pages that the module has been updated
		} catch (IOException ioe) {
			Logging.error("IO error parsing source codes from source editor, details: ");
			Logging.error(ioe.toString());
		}
	}
	
	/**
	 * Updates module and refreshes display of all pages except source editor
	 */
	// TODO: using a flag might not be a good way for potential threading problem
	protected void refreshModule(Module module) {
		refreshing = true;
		// point to the new module and refresh the display of all pages
		modulePrefsPage.changeModule(module);
		contentsPage.changeModule(module);
		localePage.changeModule(module);
		userPrefsPage.changeModule(module);
		refreshing = false;
	}

	@Override
	public void editorDirtyStateChanged() {
		if (isDirty() && !refreshing) {
			modulePrefsPage.updateModel();
			contentsPage.updateModel();
			userPrefsPage.updateModel();
			localePage.refreshModule();
			
			// The following codes update the source codes in the source editor
			String newSourceCodes = GadgetXmlSerializer.serialize(module);
			IDocument document = sourceEditor.getDocumentProvider().getDocument(getEditorInput());
			String oldSourceCodes = document.get();
			if (!newSourceCodes.equals(oldSourceCodes)) {
				document.set(newSourceCodes);
			}
			
			if (outlinePage != null) {
				outlinePage.refresh();
			}
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		commitPages(true);
		if (getActivePage() == 4 && sourceEditor.isDirty()) {
			Logging.info("Parsing source codes ...");
			parseSourceCodesAndRefreshPages();
		}
		sourceEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public String getSourceEditorContents() {
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
		String content = getSourceEditorContents();
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
