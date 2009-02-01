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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.people;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;

public class PersonView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.PersonView";
	
	private Action reloadAction;
	
	private PeopleBlock block;

	public PersonView() {
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		reloadAction = new Action() {
			@Override
			public void run() {
				loadPeople();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload people.");
		reloadAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/action_refresh.gif"));
	}
	
	protected void createForm(Composite parent) {
		IManagedForm managedForm = new ManagedForm(parent);
		block = new PeopleBlock(this);
		block.createContent(managedForm);
	}
	
	public void setFocus() {
	}
	
	public void loadPeople() {
		try {
			PersonService personService = Activator.getDefault().getPersonService();
			java.util.List<Person> people = personService.getPeople();
			block.setPeople(people);
		} catch(ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}

	public void connectedDatabase() {
		loadPeople();
	}
	
}