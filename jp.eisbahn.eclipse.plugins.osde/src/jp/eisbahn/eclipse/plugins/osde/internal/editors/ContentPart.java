package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.gadgets.Module;
import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;
import com.google.gadgets.Module.Content;

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

public class ContentPart extends AbstractFormPart {
	
	private ContentPage page;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
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
	
	private boolean initializing = true;;
	
	public ContentPart(ContentPage page) {
		super();
		this.page = page;
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
		htmlButton = createRadio(parent, toolkit, "Use the HTML type for this view.", 2);
		//
		editor = new SourceViewer(parent, null, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		editor.getTextWidget().setLayoutData(layoutData);
		Document document = new Document();
		editor.setDocument(document);
		//
		urlButton = createRadio(parent, toolkit, "Use the URL type for this view.", 2);
		//
		createLabel(parent, toolkit, "Location URL:");
		hrefText = createText(parent, toolkit, modifyListener);
		//
		notSupportButton = createRadio(parent, toolkit, "Not support this view.", 2);
	}


}
