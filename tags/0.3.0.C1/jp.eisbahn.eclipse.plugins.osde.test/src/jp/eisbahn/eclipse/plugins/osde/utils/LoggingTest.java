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
package jp.eisbahn.eclipse.plugins.osde.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.views.log.AbstractEntry;
import org.eclipse.ui.internal.views.log.Activator;
import org.eclipse.ui.internal.views.log.LogEntry;
import org.eclipse.ui.internal.views.log.LogView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("restriction")
public class LoggingTest {

	private static final String LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView";
	private static final String OSDE_PERSPECTIVE_ID = "jp.eisbahn.eclipse.plugins.osde.perspective";
	private static final int LOGGING_COUNT = 1000;

	private static final int[] LOG_LEVEL =
		new int[] {/*IStatus.OK,*/	IStatus.INFO, IStatus.WARNING, IStatus.ERROR };

	private IDialogSettings settings;
	private LogView logView;

	@Before
	public void setUp() throws Exception{
		Activator.getDefault().getDialogSettings().addNewSection(LogView.class.getName());
		settings = Activator.getDefault().getDialogSettings().getSection(LogView.class.getName());
		settings.put(LogView.P_USE_LIMIT, "false");
		settings.put(LogView.P_LOG_LIMIT, 50);
		settings.put(LogView.P_LOG_INFO, "true");
		settings.put(LogView.P_LOG_OK, "");
		settings.put(LogView.P_LOG_WARNING, "true");
		settings.put(LogView.P_LOG_ERROR, "true");
		settings.put(LogView.P_SHOW_ALL_SESSIONS, "true");

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		PlatformUI.getWorkbench().showPerspective(OSDE_PERSPECTIVE_ID, window);

		logView = (LogView) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage().showView(LOG_VIEW_ID);
	}

	@After
	public void tearDown() throws Exception {
		logView.getViewSite().getPage().close();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		PlatformUI.getWorkbench().showPerspective(OSDE_PERSPECTIVE_ID, window);
	}

	@Test
	public void testConcurrentLogging() throws Exception {

		Random random = new Random();
		Map<String, Integer> dispatchedLogs = new HashMap<String, Integer>();

		// prepare log messages
		for (int i = 0; i < LOGGING_COUNT; i++) {
			dispatchedLogs.put(String.valueOf(random.nextLong()),
				LOG_LEVEL[Math.abs(random.nextInt() % LOG_LEVEL.length)]);
		}

		// dispatch log messages
		Display display = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell().getDisplay();

		for (final Entry<String, Integer> e : dispatchedLogs.entrySet()) {
			display.asyncExec(new Runnable() {
				public void run() {
					switch (e.getValue()) {
					case IStatus.INFO:
						Logging.info(e.getKey());
						break;
					case IStatus.WARNING:
						Logging.warn(e.getKey());
						break;
					case IStatus.ERROR:
						Logging.error(e.getKey());
						break;
					default:
						break;
					}
				};
			});
		}

		// wait for logging
		delay(3000);

		/***
		 * Fetch the log messages from ErrorLog view
		 * and compare with dispatched logs.
		 *
		 * Remove the entry in the dispatched logs
		 * if any identical entry found in ErrorLog view
		 *
		 * Finally, we assert the dispatched logs should be empty.
		 * */
		for (AbstractEntry entry : logView.getElements()) {
			if (entry instanceof LogEntry) {
				LogEntry e = (LogEntry) entry;
				String k = e.getMessage();
				Integer v = e.getSeverity();

				// match and consume
				if(v.equals(dispatchedLogs.get(k))){
					dispatchedLogs.remove(k); // remove the matched log message.
				}
			}
		}

		// if everything is consumed, the number of result should be equals to 0.
		assertEquals(
			"Log messages are dismatched between ErrorLog view and dispatched logs.",
			0, dispatchedLogs.size());
		logView.getViewSite().getPage().close();
	}

	void delay(long delay) {
		Display display = Display.getCurrent();
		if (display != null) {
			long endAt = System.currentTimeMillis() + delay;
			while (System.currentTimeMillis() < endAt) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
				display.update();
			}
		} else {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ignored) {
			}
		}
		while(Job.getJobManager().currentJob() != null){
			delay(500);
		}
	}

}
