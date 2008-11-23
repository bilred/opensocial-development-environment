package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.google.gadgets.Module;
import com.google.gadgets.ViewName;

public class GadgetXmlEditor extends FormEditor {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.editors.GadgetXmlEditor";
	
	private Module module;
	private JAXBContext context;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		try {
			IFile file = (IFile)input.getAdapter(IResource.class);
			context = JAXBContext.newInstance(Module.class);
			Unmarshaller um = context.createUnmarshaller();
			module = (Module)um.unmarshal(file.getContents());
		} catch (JAXBException e) {
			// TODO 例外処理
			throw new PartInitException(e.getErrorCode(), e);
		} catch (CoreException e) {
			// TODO 例外処理
			throw new PartInitException(e.getMessage(), e);
		}
		
	}

	@Override
	protected void addPages() {
		try {
			// ModulePrefs関連のページを追加
			ModulePrefsPage modulePrefsPage = new ModulePrefsPage(this, module);
			addPage(modulePrefsPage);
			// 各ビューのページを追加
			ViewName[] viewNames = ViewName.values();
			for (ViewName viewName : viewNames) {
				ContentPage contentPage = new ContentPage(this, module, viewName);
				addPage(contentPage);
			}
		} catch(PartInitException e) {
			// TODO あとで修正
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			commitPages(true);
			IFile file = (IFile)getEditorInput().getAdapter(IResource.class);
			Marshaller ma = context.createMarshaller();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ma.marshal(module, out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			file.setContents(in, true, false, monitor);
			editorDirtyStateChanged();
		} catch (JAXBException e) {
			// TODO 例外処理
			throw new IllegalStateException(e);
		} catch (CoreException e) {
			// TODO 例外処理
			throw new IllegalStateException(e);
		}
		
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
