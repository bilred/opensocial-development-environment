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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.basic.ModulePrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.contents.ContentsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.locale.LocalePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefsPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

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
	
	private class GadgetXmlOutlinePage extends ContentOutlinePage {
		
		private GadgetXmlEditor editor;
	
		public GadgetXmlOutlinePage(GadgetXmlEditor editor) {
			super();
			this.editor = editor;
		}

		@Override
		public void createControl(Composite parent) {
			super.createControl(parent);
			TreeViewer treeViewer = getTreeViewer();
			treeViewer.setContentProvider(new ITreeContentProvider() {

				public Object[] getChildren(Object parentElement) {
					ElementModel model = (ElementModel)parentElement;
					return model.getChildren().toArray();
				}

				public Object getParent(Object element) {
					ElementModel model = (ElementModel)element;
					return model.getParent();
				}

				public boolean hasChildren(Object element) {
					ElementModel model = (ElementModel)element;
					return !model.getChildren().isEmpty();
				}

				public Object[] getElements(Object inputElement) {
					return new Object[] {inputElement};
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				
			});
			//
			try {
				String source = editor.getSource();
				ElementModel module = new GadgetXmlParser().parse(source);
				treeViewer.setInput(module);
			} catch(SAXParseException e) {
				e.printStackTrace();
			} catch(SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void refresh() {
			
		}
		
	}
	
	private class GadgetXmlParser {
	
		private class HandlerImpl extends DefaultHandler {
			
			private ElementModel root;
			private Stack<ElementModel> parentStack = new Stack<ElementModel>();
			private Locator locator;

			@Override
			public void setDocumentLocator(Locator locator) {
				this.locator = locator;
			}

			@Override
			public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
				ElementModel model = new ElementModel();
				model.setName(name);
				model.setLineNumber(locator.getLineNumber());
				for (int i = 0; i < attributes.getLength(); i++) {
					model.putAttribute(attributes.getQName(i), attributes.getValue(i));
				}
				if (root == null) {
					root = model;
					parentStack.push(model);
				} else {
					ElementModel parent = parentStack.peek();
					parent.addChild(model);
					parentStack.push(model);
				}
			}
			
			@Override
			public void endElement(String uri, String localName, String name) throws SAXException {
				parentStack.pop();
			}

			public ElementModel getResult() {
				return root;
			}
		}

		public ElementModel parse(String source) throws ParserConfigurationException, SAXException, IOException {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			StringReader reader = new StringReader(source);
			HandlerImpl handler = new HandlerImpl();
			parser.parse(new InputSource(reader), handler);
			return handler.getResult();
		}
		
	}
	
	private class ElementModel {
		
		private String name;
		private int lineNumber;
		private List<ElementModel> children;
		private Map<String, String> attributes;
		private ElementModel parent;
		
		public ElementModel() {
			super();
			children = new ArrayList<ElementModel>();
			attributes = new HashMap<String, String>();
		}
		
		public ElementModel getParent() {
			return parent;
		}

		public void setParent(ElementModel parent) {
			this.parent = parent;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public int getLineNumber() {
			return lineNumber;
		}
		
		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}
		
		public List<ElementModel> getChildren() {
			return children;
		}
		
		public void addChild(ElementModel child) {
			children.add(child);
			child.setParent(this);
		}
		
		public void putAttribute(String name, String value) {
			attributes.put(name, value);
		}
		
		public Map<String, String> getAttributes() {
			return attributes;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}

}
