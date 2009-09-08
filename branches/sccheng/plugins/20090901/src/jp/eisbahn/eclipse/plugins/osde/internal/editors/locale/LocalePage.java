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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import java.util.List;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.gadgets.model.Module;
import com.google.gadgets.model.Module.ModulePrefs.Locale;

public class LocalePage extends FormPage {

	private Module module;
	
	private MessageBundleBlock block;
	
	public Module getModule() {
		return module;
	}

	public LocalePage(FormEditor editor, Module module) {
		super(editor, null, "i18n (Message Bundle)");
		this.module = module;
		block = new MessageBundleBlock(this);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		block.createContent(managedForm);
	}
	
	public List<Locale> getLocales() {
		return module.getModulePrefs().getLocales();
	}

	public void changeModule(Module module) {
		this.module = module;
		block.refreshModule();
	}
	
	public void refreshModule() {
		block.refreshModule();
	}
}