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
package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.preferences.PreferenceConstants;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

public class ShindigLauncher {

	public static boolean launchWithConfirm(Shell shell, final IWorkbenchPart targetPart) {
		if (MessageDialog.openConfirm(shell, "Confirm", "Shindig not started yet. Would you like to start Shindig?")) {
			ShindigLauncher.launch(shell, targetPart);
			return true;
		} else {
			return false;
		}
	}

	public static void launch(Shell shell, final IWorkbenchPart targetPart) {
		Activator.getDefault().setRunningShindig(true);
		try {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunch[] launches = manager.getLaunches();
			for (ILaunch launch : launches) {
				String name = launch.getLaunchConfiguration().getName();
				if (name.equals("Shindig Database") || name.equals("Apache Shindig")) {
					launch.terminate();
				}
			}
			
			IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			createConfigFileForHibernate(prefs);
			
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			int delay = 0;
			
			// The following codes launch Database and then launch Shindig
			// If the user specifies an external database, we won't launch internal database
			// Launch Database for Shindig to connect to
			if (prefs.getBoolean(PreferenceConstants.USE_INTERNAL_DATABASE)) {
				for (int i = 0; i < configurations.length; ++i) {
					if (configurations[i].getName().equals("Shindig Database")) {
						final ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								try {
									DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
								} catch (CoreException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				}
				delay = 3000;
			}
			shell.getDisplay().timerExec(delay, new Runnable() {
				public void run() {
					Activator.getDefault().connect(targetPart.getSite().getWorkbenchWindow());
				}
			});
			
			// Launch Shindig container
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Apache Shindig")) {
					final ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
					shell.getDisplay().timerExec(3000, new Runnable() {
						public void run() {
							try {
								DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createConfigFileForHibernate(IPreferenceStore prefs) {
		FileOutputStream fos = null;
		try {
			InputStreamReader in = new InputStreamReader(
					ShindigLauncher.class.getResourceAsStream("/shindig/osde_hibernate.cfg.xml.tmpl"), "UTF-8");
			StringWriter out = new StringWriter();
			IOUtils.copy(in, out);
			String code = out.toString();
			if (prefs.getBoolean(PreferenceConstants.USE_INTERNAL_DATABASE)) {
				code = code.replace("$driver_class$", "org.h2.Driver");
				code = code.replace("$url$", "jdbc:h2:tcp://localhost:9092/shindig");
				code = code.replace("$username$", "sa");
				code = code.replace("$password$", "");
				code = code.replace("$dialect$", "H2");
			} else {
				if (prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_TYPE).equals("MySQL")) {
					code = code.replace("$driver_class$", "com.mysql.jdbc.Driver");
					String url = "jdbc:mysql://";
					url += prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_HOST);
					String port = prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_PORT);
					if (StringUtils.isNotEmpty(port)) {
						url += ":" + port;
					}
					url += "/" + prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_NAME);
					code = code.replace("$url$", url);
					code = code.replace("$username$", prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_USERNAME));
					code = code.replace("$password$", prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_PASSWORD));
					code = code.replace("$dialect$", "MySQL");
				} else if (prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_TYPE).equals("Oracle")) {
					code = code.replace("$driver_class$", "oracle.jdbc.driver.OracleDriver");
					String url = "jdbc:oracle:thin:@";
					url += prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_HOST);
					String port = prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_PORT);
					if (StringUtils.isNotEmpty(port)) {
						url += ":" + port;
					}
					url += ":" + prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_NAME);
					code = code.replace("$url$", url);
					code = code.replace("$username$", prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_USERNAME));
					code = code.replace("$password$", prefs.getString(PreferenceConstants.EXTERNAL_DATABASE_PASSWORD));
					code = code.replace("$dialect$", "Oracle");
				}
			}
			File file = new File(HibernateUtils.configFileDir, HibernateUtils.configFileName);
			fos = new FileOutputStream(file);
			ByteArrayInputStream bytes = new ByteArrayInputStream(code.getBytes("UTF-8"));
			IOUtils.copy(bytes, fos);
		} catch(IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

}
