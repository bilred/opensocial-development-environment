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
package com.googlecode.osde.internal.editors.pref;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

public class UserPrefsBlock extends MasterDetailsBlock {
	
	private UserPrefsPart userPrefsPart;
	
	private UserPrefsPage page;
	
	public UserPrefsBlock(UserPrefsPage page) {
		super();
		this.page = page;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		userPrefsPart = new UserPrefsPart(parent, managedForm, page);
		managedForm.addPart(userPrefsPart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new UserPrefPage(page);
		detailsPart.registerPage(UserPrefModel.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(UserPrefModel.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof UserPrefModel) {
					return UserPrefModel.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{45, 55});
	}

	public void updateUserPrefModel() {
		userPrefsPart.markDirty();
	}

	public void updateModel() {
		if (userPrefsPart != null) {
			userPrefsPart.setValuesToModule();
		}
	}

	public void changeModel() {
		if (userPrefsPart != null) {
			userPrefsPart.changeModel();
		}
	}
	
}