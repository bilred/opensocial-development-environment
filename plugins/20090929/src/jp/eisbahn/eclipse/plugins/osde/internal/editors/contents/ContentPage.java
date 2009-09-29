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

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.ViewType;

public class ContentPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private ContentsPage page;
	private ContentModel model;
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			if (!initializing) {
				makeDirty();
			}
		}
	};

	private Button htmlButton;

	private Button urlButton;

	private Text hrefText;

	private SourceViewer editor;
	
	private boolean initializing = true;
	
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			changeComponentEnabled();
		}
	};
	
	public ContentPage(ContentsPage page) {
		super();
		this.page = page;
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();
		// 
		Section contentSection = toolkit.createSection(parent, Section.TITLE_BAR);
		contentSection.setText("Content");
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		contentSection.setLayoutData(layoutData);
		Composite contentPane = toolkit.createComposite(contentSection);
		contentPane.setLayout(new GridLayout(2, false));
		contentSection.setClient(contentPane);
		//
		htmlButton = createRadio(contentPane, toolkit, "Use the HTML type for this view.", 2, modifyListener);
		htmlButton.addSelectionListener(selectionListener);
		//
		editor = new SourceViewer(contentPane, null, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		layoutData.heightHint = 250;
		editor.getTextWidget().setLayoutData(layoutData);
		Document document = new Document();
		IDocumentPartitioner partitioner = new FastPartitioner(
				new HtmlContentPartitionScanner(),
				new String[] {
					HtmlContentPartitionScanner.TOKEN_SCRIPT,
					HtmlContentPartitionScanner.TOKEN_HTML_COMMENT,
					HtmlContentPartitionScanner.TOKEN_TAG});
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		editor.setDocument(document);
		editor.configure(new HtmlContentConfiguration());
		document.addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
			public void documentChanged(DocumentEvent event) {
				if (!initializing) {
					makeDirty();
				}
			}
		});
		//
		urlButton = createRadio(contentPane, toolkit, "Use the URL type for this view.", 2, modifyListener);
		urlButton.addSelectionListener(selectionListener);
		//
		createLabel(contentPane, toolkit, "Location URL:");
		hrefText = createText(contentPane, toolkit, modifyListener);
	}

	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		initializing = true;
		model = (ContentModel)((IStructuredSelection)selection).getFirstElement();
		displayInitialValue();
		changeComponentEnabled();
		initializing = false;
	}
	
	private void displayInitialValue() {
		hrefText.setText("http://");
		ViewType type = model.getType();
		if (ViewType.html.equals(type)) {
			htmlButton.setSelection(true);
			urlButton.setSelection(false);
			String body = model.getBody();
			editor.getDocument().set((body == null) ? "" : body);
		} else if (ViewType.url.equals(type)) {
			urlButton.setSelection(true);
			htmlButton.setSelection(false);
			String href = model.getHref();
			hrefText.setText((href == null) ? "" : href);
		} else {
			// illegal
			throw new IllegalStateException("Unknown type.");
		}
	}
	
	private void makeDirty() {
		setValuesToModel();
		page.updateContentsModel();
	}
	
	private void changeComponentEnabled() {
		boolean htmlButtonSelected = htmlButton.getSelection();
		boolean urlButtonSelected = urlButton.getSelection();
		editor.getTextWidget().setEnabled(htmlButtonSelected);
		hrefText.setEnabled(urlButtonSelected);
	}
	
	private void setValuesToModel() {
		if (htmlButton.getSelection()) {
			model.setType(ViewType.html);
			model.setBody(editor.getDocument().get());
			model.setHref(null);
		} else if (urlButton.getSelection()) {
			model.setType(ViewType.url);
			model.setBody(null);
			model.setHref(hrefText.getText());
		}
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}
	
}