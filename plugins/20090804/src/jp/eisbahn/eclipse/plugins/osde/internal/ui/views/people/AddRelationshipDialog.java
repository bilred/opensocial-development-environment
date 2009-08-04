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

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class AddRelationshipDialog extends TitleAreaDialog {

	private List<Person> people;
	private Combo groupIdCombo;
	private TableViewer personList;
	private String groupId;
	private Person target;
	
	private ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};
	
	public AddRelationshipDialog(Shell shell, List<Person> people) {
		super(shell);
		this.people = people;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Add relationship");
		setMessage("Please select person and input group ID.");
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		panel.setLayoutData(layoutData);
		//
		Label label = new Label(panel, SWT.NONE);
		label.setText("Group ID:");
		groupIdCombo = new Combo(panel, SWT.DROP_DOWN);
		groupIdCombo.add("friends");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		groupIdCombo.setLayoutData(layoutData);
		groupIdCombo.addModifyListener(modifyListener);
		//
		label = new Label(panel, SWT.NONE);
		label.setText("Person:");
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(layoutData);
		Table table = new Table(panel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("ID");
		column.setWidth(250);
		personList = new TableViewer(table);
		personList.setContentProvider(new PersonListContentProvider());
		personList.setLabelProvider(new PersonListLabelProvider());
		personList.setInput(people);
		//
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		groupId = groupIdCombo.getText();
		target = (Person)((IStructuredSelection)personList.getSelection()).getFirstElement();;
		setReturnCode(OK);
		close();
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public Person getTarget() {
		return target;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	private boolean validate() {
		String groupId = groupIdCombo.getText().trim();
		if (StringUtils.isEmpty(groupId)) {
			setMessage("Group ID is required.", IMessageProvider.ERROR);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
		}
		setMessage(null);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;
	}

}
