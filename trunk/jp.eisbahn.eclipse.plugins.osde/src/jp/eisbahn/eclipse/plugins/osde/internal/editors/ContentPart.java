package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
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
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;
import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;
import com.google.gadgets.Module.Content;

public class ContentPart extends AbstractFormPart {
	
	private ContentPage page;
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			if (!initializing) {
				markDirty();
			}
		}
	};

	private Button notSupportButton;

	private Button htmlButton;

	private Button urlButton;

	private Text hrefText;

	private SourceViewer editor;
	
	private boolean initializing = true;
	
	private ObjectFactory objectFactory;
	
	private SelectionListener selectionListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			changeComponentEnabled();
		}
	};
	
	public ContentPart(ContentPage page) {
		super();
		this.page = page;
		objectFactory = new ObjectFactory();
	}
	
	private Module getModule() {
		return page.getModule();
	}
	
	private ViewName getViewName() {
		return page.getViewName();
	}
	
	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
		createControls(form);
		displayInitialValue();
		initializing =false;
	}

	private void displayInitialValue() {
		Content content = getContent(getModule());
		if (content != null) {
			hrefText.setText("http://");
			String type = content.getType();
			if (ViewType.html.toString().equals(type)) {
				htmlButton.setSelection(true);
				editor.getDocument().set(content.getValue());
			} else if (ViewType.url.toString().equals(type)) {
				urlButton.setSelection(true);
				hrefText.setText(content.getHref());
			} else {
				notSupportButton.setSelection(true);
			}
		} else {
			notSupportButton.setSelection(true);
		}
	}
	
	private Content getContent(Module module) {
		List<Content> contentList = module.getContent();
		for (Content content : contentList) {
			if (getViewName().toString().equals(content.getView())) {
				return content;
			}
		}
		return null;
	}

	private void createControls(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getViewName().getDisplayName() + " view");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		form.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Composite parent = toolkit.createComposite(form.getBody());
		layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		htmlButton = createRadio(parent, toolkit, "Use the HTML type for this view.", 2, modifyListener);
		htmlButton.addSelectionListener(selectionListener);
		//
		editor = new SourceViewer(parent, null, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		editor.getTextWidget().setLayoutData(layoutData);
		Document document = new Document();
		editor.setDocument(document);
		document.addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
			public void documentChanged(DocumentEvent event) {
				if (!initializing) {
					markDirty();
				}
			}
		});
		//
		urlButton = createRadio(parent, toolkit, "Use the URL type for this view.", 2, modifyListener);
		urlButton.addSelectionListener(selectionListener);
		//
		createLabel(parent, toolkit, "Location URL:");
		hrefText = createText(parent, toolkit, modifyListener);
		//
		notSupportButton = createRadio(parent, toolkit, "Not support this view.", 2, modifyListener);
		notSupportButton.addSelectionListener(selectionListener);
	}

	private void changeComponentEnabled() {
		boolean htmlButtonSelected = htmlButton.getSelection();
		boolean urlButtonSelected = urlButton.getSelection();
		editor.getTextWidget().setEnabled(htmlButtonSelected);
		hrefText.setEnabled(urlButtonSelected);
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
		Content content = getContent(module);
		if (notSupportButton.getSelection()) {
			if (content != null) {
				module.getContent().remove(content);
			}
		} else if (htmlButton.getSelection()) {
			if (content == null) {
				content = objectFactory.createModuleContent();
				module.getContent().add(content);
			}
			content.setHref(null);
			content.setType(ViewType.html.toString());
			content.setValue(editor.getDocument().get());
			content.setView(getViewName().toString());
		} else if (urlButton.getSelection()) {
			if (content == null) {
				content = objectFactory.createModuleContent();
				module.getContent().add(content);
			}
			content.setHref(hrefText.getText());
			content.setType(ViewType.url.toString());
			content.setValue(null);
			content.setView(getViewName().toString());
		} else {
			throw new IllegalStateException("Invalid view type.");
		}
	}

}
