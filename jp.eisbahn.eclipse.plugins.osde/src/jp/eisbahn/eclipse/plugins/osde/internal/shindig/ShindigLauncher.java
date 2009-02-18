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

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

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
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			// launch shindig & database
			for (int i = 0; i < configurations.length; i++) {
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
			shell.getDisplay().timerExec(3000, new Runnable() {
				public void run() {
					Activator.getDefault().connect(targetPart.getSite().getWorkbenchWindow());
				}
			});
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

}
