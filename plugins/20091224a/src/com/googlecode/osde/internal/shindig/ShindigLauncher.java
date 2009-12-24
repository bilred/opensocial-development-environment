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
package com.googlecode.osde.internal.shindig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.utils.Logger;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

public class ShindigLauncher {
    private static final Logger logger = new Logger(ShindigLauncher.class);

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
			//
			OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
			createConfigFileForHibernate(config);
			//
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			int delay = 0;
			
			// The following codes launch Database and then launch Shindig
			// If the user specifies an external database, we won't launch internal database
			// Launch Database for Shindig to connect to
			if (config.isUseInternalDatabase()) {
				for (int i = 0; i < configurations.length; ++i) {
					if (configurations[i].getName().equals("Shindig Database")) {
						final ILaunchConfigurationWorkingCopy wc = configurations[i].getWorkingCopy();
						shell.getDisplay().syncExec(new Runnable() {
							public void run() {
								try {
									DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
								} catch (CoreException e) {
									logger.error("Launching Shindig Database failed.", e);
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
								logger.error("Launching Apache Shindig failed.", e);
							}
						}
					});
				}
			}
		} catch (CoreException e) {
			logger.error("Launching Apache Shindig or Shindig Database failed.", e);
		}
	}

	private static void createConfigFileForHibernate(OsdeConfig config) {
		FileOutputStream fos = null;
		try {
			InputStreamReader in = new InputStreamReader(
					ShindigLauncher.class.getResourceAsStream("/shindig/osde_hibernate.cfg.xml.tmpl"), "UTF-8");
			StringWriter out = new StringWriter();
			IOUtils.copy(in, out);
			String code = out.toString();
			if (config.isUseInternalDatabase()) {
				code = code.replace("$driver_class$", "org.h2.Driver");
				code = code.replace("$url$", "jdbc:h2:tcp://localhost:9092/shindig");
				code = code.replace("$username$", "sa");
				code = code.replace("$password$", "");
				code = code.replace("$dialect$", "H2");
				String databaseDir = config.getDatabaseDir();
				File dbFile = new File(databaseDir, "shindig.data.db");
				code = code.replace("$hbm2ddl$", dbFile.isFile() ? "update" : "create");
			} else {
				if (config.getExternalDatabaseType().equals("MySQL")) {
					code = code.replace("$driver_class$", "com.mysql.jdbc.Driver");
					String url = "jdbc:mysql://";
					url += config.getExternalDatabaseHost();
					String port = config.getExternalDatabasePort();
					if (StringUtils.isNotEmpty(port)) {
						url += ":" + port;
					}
					url += "/" + config.getExternalDatabaseName();
					code = code.replace("$url$", url);
					code = code.replace("$username$", config.getExternalDatabaseUsername());
					code = code.replace("$password$", config.getExternalDatabasePassword());
					code = code.replace("$dialect$", "MySQL");
				} else if (config.getExternalDatabaseType().equals("Oracle")) {
					code = code.replace("$driver_class$", "oracle.jdbc.driver.OracleDriver");
					String url = "jdbc:oracle:thin:@";
					url += config.getExternalDatabaseHost();
					String port = config.getExternalDatabasePort();
					if (StringUtils.isNotEmpty(port)) {
						url += ":" + port;
					}
					url += ":" + config.getExternalDatabaseName();
					code = code.replace("$url$", url);
					code = code.replace("$username$", config.getExternalDatabaseUsername());
					code = code.replace("$password$", config.getExternalDatabasePassword());
					code = code.replace("$dialect$", "Oracle");
				}
			}
			File file = new File(HibernateUtils.configFileDir, HibernateUtils.configFileName);
			fos = new FileOutputStream(file);
			ByteArrayInputStream bytes = new ByteArrayInputStream(code.getBytes("UTF-8"));
			IOUtils.copy(bytes, fos);
		} catch(IOException e) {
			logger.error("Creating the configuration file for H2Database failed.", e);
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

}
