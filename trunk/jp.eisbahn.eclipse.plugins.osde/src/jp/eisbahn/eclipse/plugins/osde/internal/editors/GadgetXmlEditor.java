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
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jp.eisbahn.eclipse.plugins.osde.internal.editors.basic.ModulePrefsPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.content.ContentPage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.locale.LocalePage;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.pref.UserPrefsPage;

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
			ModulePrefsPage modulePrefsPage = new ModulePrefsPage(this, module);
			addPage(modulePrefsPage);
			LocalePage messageBundlePage = new LocalePage(this, module);
			addPage(messageBundlePage);
			UserPrefsPage userPrefsPage = new UserPrefsPage(this, module);
			addPage(userPrefsPage);
			ViewName[] viewNames = ViewName.values();
			for (ViewName viewName : viewNames) {
				ContentPage contentPage = new ContentPage(this, module, viewName);
				addPage(contentPage);
			}
		} catch(PartInitException e) {
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
			throw new IllegalStateException(e);
		} catch (CoreException e) {
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
