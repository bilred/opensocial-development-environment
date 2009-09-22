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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Set;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.AbstractView;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShindigMonitorView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.ShindigMonitorView";
	
	private HttpLogsBlock block;
	
	private Action monitorAction;
	
	private HttpLogMonitor monitor;

	public ShindigMonitorView() {
	}
	
	@Override
	public void dispose() {
		if (monitor != null) {
			monitor.stopMonitor();
		}
		super.dispose();
	}

	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(monitorAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(monitorAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(monitorAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		monitorAction = new Action("Monitoring", SWT.TOGGLE) {
			@Override
			public void run() {
				if (isChecked()) {
					monitor = new HttpLogMonitor();
					monitor.startMonitor();
					setImageDescriptor(
							Activator.getDefault().getImageRegistry().getDescriptor("icons/16-circle-red-remove.gif"));
				} else {
					monitor.stopMonitor();
					setImageDescriptor(
							Activator.getDefault().getImageRegistry().getDescriptor("icons/16-arrow-right.gif"));
				}
			}
		};
		monitorAction.setText("Monitoring");
		monitorAction.setToolTipText("Monitoring HTTP logs.");
		monitorAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/16-arrow-right.gif"));
	}

	@Override
	protected void createForm(Composite parent) {
		IManagedForm managedForm = new ManagedForm(parent);
		block = new HttpLogsBlock(this);
		block.createContent(managedForm);
	}

	@Override
	public void setFocus() {
	}
	
	private class HttpLogMonitor {
		
		private boolean running = false;
		
		public HttpLogMonitor() {
			super();
		}
		
		public void stopMonitor() {
			running = false;
		}

		public void startMonitor() {
			running = true;
//			MessageDialog.openInformation(getSite().getShell(), "Http log monitor", "Http log monitor has been just starting.");
			getSite().getShell().getDisplay().timerExec(1000, new Monitor());
		}
		
		private class Monitor implements Runnable {

			public void run() {
				if (running) {
					Set<HttpLog> logs = block.getLogs();
					BufferedReader in = null;
					try {
						File file = new File(System.getProperty("java.io.tmpdir"), "osde_shindig.log");
						in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						String line = null;
						while((line = in.readLine()) != null) {
							try {
								JSONParser parser = new JSONParser();
								JSONObject json = (JSONObject)parser.parse(new StringReader(line));
								HttpLog log = new HttpLog(json);
								if (!logs.contains(log)) {
									logs.add(log);
								}
							} catch (Exception e) {
								Logging.warn("Parsing the log file failed.", e);
							}
						}
					} catch(FileNotFoundException e) {
						// nop
					} catch(IOException e) {
						Logging.warn("Parsing the log file failed.", e);
					} finally {
						IOUtils.closeQuietly(in);
					}
					block.refreshLogs();
					getSite().getShell().getDisplay().timerExec(1000, this);
				} else {
					block.resetLogs();
				}
			}
			
		}

	}
	
}
