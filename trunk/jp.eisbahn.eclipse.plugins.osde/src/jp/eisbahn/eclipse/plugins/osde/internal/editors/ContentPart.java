package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.gadgets.Module;
import com.google.gadgets.ViewName;

import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createRadio;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.editors.ComponentUtils.createText;

public class ContentPart extends AbstractFormPart {
	
	private ContentPage page;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			markDirty();
		}
	};
	
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
	}

	private void displayInitialValue() {
		// TODO Auto-generated method stub
		
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
		createRadio(parent, toolkit, "Use the HTML type for this view.", 2);
		//
		SourceViewer editor = new SourceViewer(parent, null, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		editor.getTextWidget().setLayoutData(layoutData);
		Document document = new Document();
		editor.setDocument(document);
		//
		createRadio(parent, toolkit, "Use the URL type for this view.", 2);
		//
		createLabel(parent, toolkit, "Location URL:");
		createText(parent, toolkit, modifyListener);
	}


}
