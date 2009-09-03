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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.preferences.PreferenceConstants;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;

public class DatabaseLaunchConfigurationCreator {

	public final static String H2_LIB_PATH = "/libs/h2-1.1.117.jar";

	public void delete(IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Deleting the launch configuration for Database.", 1);
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Shindig Database")) {
					configurations[i].delete();
				}
			}
		} finally {
			monitor.done();
		}
	}

	public void create(IProgressMonitor monitor) throws CoreException, MalformedURLException, IOException {
		try {
			monitor.beginTask("Creating the launch configuration for Database", 3);
			monitor.subTask("Deleting the setting that already exists.");
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Shidig Database")) {
					configurations[i].delete();
				}
			}
			monitor.worked(1);
			monitor.subTask("Building the classpath.");
			IPath systemLibs = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
					systemLibs, IRuntimeClasspathEntry.STANDARD_CLASSES);
			systemLibsEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
			IRuntimeClasspathEntry h2Entry = createRuntimeClasspathEntry(H2_LIB_PATH);
			ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "Shindig Database");
			List<String> classpath = new ArrayList<String>();
			classpath.add(systemLibsEntry.getMemento());
			classpath.add(h2Entry.getMemento());
			monitor.worked(1);
			monitor.subTask("Creating the launch configuration.");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.h2.tools.Server");
			IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			String args = "-tcp -tcpAllowOthers";
			String databaseDir = prefs.getString(PreferenceConstants.DATABASE_DIR);
			if (StringUtils.isNotEmpty(databaseDir)) {
				if (databaseDir.endsWith("\\")) {
					databaseDir = databaseDir.substring(0, databaseDir.length() - 1);
				}
				args += " -baseDir \"" + databaseDir + "\"";
			}
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
			wc.doSave();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}

	private IRuntimeClasspathEntry createRuntimeClasspathEntry(String path) throws MalformedURLException, IOException {
		URL url = getBundleEntryUrl(path);
		IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(url.getPath()));
		entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
		return entry;
	}

	private URL getBundleEntryUrl(String path) throws MalformedURLException, IOException {
		return FileLocator.toFileURL(new URL(
				Activator.getDefault().getBundle().getEntry(path).toExternalForm()));
	}

}
