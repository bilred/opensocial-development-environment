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
package com.googlecode.osde.internal.ui.views.monitor;


import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

class HttpLogsBlock extends MasterDetailsBlock {

	private HttpLogsPart httpLogsPart;
	private ShindigMonitorView shindigMonitorView;
	
	public HttpLogsBlock(ShindigMonitorView shindigMonitorView) {
		super();
		this.shindigMonitorView = shindigMonitorView;
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		httpLogsPart = new HttpLogsPart(parent, managedForm, shindigMonitorView);
		managedForm.addPart(httpLogsPart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new HttpLogPage(shindigMonitorView);
		detailsPart.registerPage(HttpLog.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(HttpLog.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof HttpLog) {
					return HttpLog.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{50, 50});
	}
	
	public Set<HttpLog> getLogs() {
		return httpLogsPart.getLogs();
	}

	public void refreshLogs() {
		httpLogsPart.refreshLogs();
	}

	public void resetLogs() {
		httpLogsPart.resetLogs();
	}

}