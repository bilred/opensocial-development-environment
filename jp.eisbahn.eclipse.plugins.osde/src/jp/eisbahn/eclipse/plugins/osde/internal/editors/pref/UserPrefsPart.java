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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.pref;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gadgets.Module;
import com.google.gadgets.ObjectFactory;

public class UserPrefsPart extends SectionPart implements IPartSelectionListener {
	
	private UserPrefsPage page;
	
	private TableViewer supportedLocaleList;
	
	private ObjectFactory objectFactory;

	public UserPrefsPart(Composite parent, IManagedForm managedForm, UserPrefsPage page) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		this.page = page;
		createContents(getSection(), managedForm.getToolkit());
		displayInitialValue();
		objectFactory = new ObjectFactory();
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
	}

	private void displayInitialValue() {
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
	}
	
	private Module getModule() {
		return page.getModule();
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
	}
	
}
