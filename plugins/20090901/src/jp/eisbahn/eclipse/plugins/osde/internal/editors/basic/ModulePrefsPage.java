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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.basic;


import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.google.gadgets.model.Module;

public class ModulePrefsPage extends FormPage {
	
	private Module module;
	private ApplicationInformationPart applicationInformationPart;
	private FeaturesPart featuresPart;
	private IconPart iconPart;
	private ContentRewritePart contentRewritePart;
	
	public Module getModule() {
		return module;
	}

	public ModulePrefsPage(FormEditor editor, Module module) {
		super(editor, null, "Basic");
		this.module = module;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		applicationInformationPart = new ApplicationInformationPart(this);
		managedForm.addPart(applicationInformationPart);
		featuresPart = new FeaturesPart(this);
		managedForm.addPart(featuresPart);
		iconPart = new IconPart(this);
		managedForm.addPart(iconPart);
		contentRewritePart = new ContentRewritePart(this);
		managedForm.addPart(contentRewritePart);
	}

	public void updateModel() {
		if (applicationInformationPart != null) {
			applicationInformationPart.setValuesToModule();
		}
		if (featuresPart != null) {
			featuresPart.setValuesToModule();
		}
		if (contentRewritePart != null) {
			contentRewritePart.setValuesToModule();
		}
		if (iconPart != null) {
			iconPart.setValuesToModule();
		}
	}

	public void changeModule(Module module) {
		this.module = module;
		applicationInformationPart.changeModel();
		featuresPart.changeModel();
		contentRewritePart.changeModel();
		iconPart.changeModel();
	}
	
}
