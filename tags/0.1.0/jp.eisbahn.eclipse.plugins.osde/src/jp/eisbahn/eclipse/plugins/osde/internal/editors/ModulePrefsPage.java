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

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.gadgets.Module;

public class ModulePrefsPage extends FormPage {
	
	private Module module;
	
	public Module getModule() {
		return module;
	}

	public ModulePrefsPage(FormEditor editor, Module module) {
		super(editor, null, "Basic");
		this.module = module;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.addPart(new ApplicationInformationPart(this));
		managedForm.addPart(new FeaturesPart(this));
	}
	
}