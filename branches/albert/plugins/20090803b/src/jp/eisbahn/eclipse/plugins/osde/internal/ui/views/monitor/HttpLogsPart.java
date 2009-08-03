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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.monitor;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class HttpLogsPart extends SectionPart implements IPartSelectionListener {
	
	private TableViewer httpLogList;
	private ShindigMonitorView shindigMonitorView;

	public HttpLogsPart(Composite parent, IManagedForm managedForm, ShindigMonitorView shindigMonitorView) {
		super(parent, managedForm.getToolkit(), Section.TITLE_BAR);
		initialize(managedForm);
		createContents(getSection(), managedForm.getToolkit());
		this.shindigMonitorView = shindigMonitorView;
	}
	
	private void createContents(Section section, FormToolkit toolkit) {
		section.setText("HTTP logs ");
		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(1, false));
		// Log list
		Table table = toolkit.createTable(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Time");
		column.setWidth(150);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Path");
		column.setWidth(150);
		httpLogList = new TableViewer(table);
		httpLogList.setContentProvider(new HttpLogListContentProvider());
		httpLogList.setLabelProvider(new HttpLogListLabelProvider());
		final SectionPart part = new SectionPart(section);
		httpLogList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getManagedForm().fireSelectionChanged(part, event.getSelection());
			}
		});
		httpLogList.setInput(new TreeSet<HttpLog>());
		//
		section.setClient(composite);
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		httpLogList.refresh((HttpLog)((IStructuredSelection)selection).getFirstElement());
	}
	
	public Set<HttpLog> getLogs() {
		return (Set<HttpLog>)httpLogList.getInput();
	}

	public void refreshLogs() {
		httpLogList.refresh();
	}

	public void resetLogs() {
		getLogs().clear();
		httpLogList.refresh();
	}

}
