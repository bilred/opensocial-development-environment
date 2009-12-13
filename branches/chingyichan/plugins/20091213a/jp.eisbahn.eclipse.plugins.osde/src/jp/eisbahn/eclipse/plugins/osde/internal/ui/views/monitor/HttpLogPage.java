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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class HttpLogPage implements IDetailsPage {

	private IManagedForm managedForm;
	private DateFormat formatter;

	private ShindigMonitorView shindigMonitorView;
	private HttpLog log;

	private Text reqBodyText;

	private Label requestURILabel;
	private Label timestampLabel;
	private Label contentTypeLabel;
	private Label methodLabel;
	private TableViewer reqHeaderList;
	private TableViewer reqParamList;

	public HttpLogPage(ShindigMonitorView shindigMonitorView) {
		super();
		this.shindigMonitorView = shindigMonitorView;
		formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();
		// Basic
		Section basicSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		basicSection.setText("Basic");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		basicSection.setLayoutData(layoutData);
		Composite basicPane = toolkit.createComposite(basicSection);
		basicPane.setLayout(new GridLayout(2, false));
		basicSection.setClient(basicPane);
		//
		toolkit.createLabel(basicPane, "Request URI:");
		requestURILabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		requestURILabel.setLayoutData(layoutData);
		//
		toolkit.createLabel(basicPane, "Timestamp:");
		timestampLabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		timestampLabel.setLayoutData(layoutData);
		//
		toolkit.createLabel(basicPane, "Content type:");
		contentTypeLabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		contentTypeLabel.setLayoutData(layoutData);
		//
		toolkit.createLabel(basicPane, "Method:");
		methodLabel = toolkit.createLabel(basicPane, "");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		methodLabel.setLayoutData(layoutData);
		//
		// Request Parameter
		Section reqParamSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		reqParamSection.setText("Request parameters");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		reqParamSection.setLayoutData(layoutData);
		Composite reqParamPane = toolkit.createComposite(reqParamSection);
		reqParamPane.setLayout(new GridLayout(1, false));
		reqParamSection.setClient(reqParamPane);
		//
		Table reqParamTable = toolkit.createTable(reqParamPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		reqParamTable.setHeaderVisible(true);
		reqParamTable.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		reqParamTable.setLayoutData(layoutData);
		TableColumn column = new TableColumn(reqParamTable, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(reqParamTable, SWT.LEFT, 1);
		column.setText("Name");
		column.setWidth(150);
		column = new TableColumn(reqParamTable, SWT.LEFT, 2);
		column.setText("Value");
		column.setWidth(150);
		reqParamList = new TableViewer(reqParamTable);
		reqParamList.setContentProvider(new JSONListContentProvider());
		reqParamList.setLabelProvider(new JSONListLabelProvider());
		//
		// Request Body
		Section reqBodySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		reqBodySection.setText("Request body");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		reqBodySection.setLayoutData(layoutData);
		Composite reqBodyPane = toolkit.createComposite(reqBodySection);
		reqBodyPane.setLayout(new GridLayout(1, false));
		reqBodySection.setClient(reqBodyPane);
		//
		reqBodyText = toolkit.createText(reqBodyPane, "", SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.heightHint = 100;
		reqBodyText.setLayoutData(layoutData);
		//
		// Request Header
		Section reqHeaderSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		reqHeaderSection.setText("Request headers");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		reqHeaderSection.setLayoutData(layoutData);
		Composite reqHeaderPane = toolkit.createComposite(reqHeaderSection);
		reqHeaderPane.setLayout(new GridLayout(1, false));
		reqHeaderSection.setClient(reqHeaderPane);
		//
		Table reqHeaderTable = toolkit.createTable(reqHeaderPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		reqHeaderTable.setHeaderVisible(true);
		reqHeaderTable.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		reqHeaderTable.setLayoutData(layoutData);
		column = new TableColumn(reqHeaderTable, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(20);
		column = new TableColumn(reqHeaderTable, SWT.LEFT, 1);
		column.setText("Name");
		column.setWidth(150);
		column = new TableColumn(reqHeaderTable, SWT.LEFT, 2);
		column.setText("Value");
		column.setWidth(150);
		reqHeaderList = new TableViewer(reqHeaderTable);
		reqHeaderList.setContentProvider(new JSONListContentProvider());
		reqHeaderList.setLabelProvider(new JSONListLabelProvider());
	}

	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		log = (HttpLog)((IStructuredSelection)selection).getFirstElement();
		requestURILabel.setText(getValue(log, "requestURI"));
		long timestamp = (Long)log.getJSON().get("timestamp");
		Date date = new Date(timestamp);
		timestampLabel.setText(formatter.format(date));
		contentTypeLabel.setText(getValue(log, "contentType"));
		methodLabel.setText(getValue(log, "method"));
		reqHeaderList.setInput(log.getJSON().get("headerMap"));
		reqParamList.setInput(log.getJSON().get("parameterMap"));
		reqBodyText.setText(getValue(log, "body"));
	}

	private String getValue(HttpLog log, String name) {
		Object object = log.getJSON().get(name);
		if (object != null) {
			return (String)object;
		} else {
			return "";
		}
	}

}