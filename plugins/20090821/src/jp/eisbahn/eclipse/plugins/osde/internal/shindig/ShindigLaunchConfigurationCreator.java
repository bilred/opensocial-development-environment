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

public class ShindigLaunchConfigurationCreator {
	
	public void delete(IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Deleting the launch configuration for Apache Shindig.", 1);
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Apache Shindig")) {
					configurations[i].delete();
				}
			}
		} finally {
			monitor.done();
		}
	}
	
	public void create(IProgressMonitor monitor) throws CoreException, MalformedURLException, IOException {
		try {
			monitor.beginTask("Creating the launch configuration for Apache Shindig", 3);
			monitor.subTask("Deleting the setting that already exists.");
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Apache Shindig")) {
					configurations[i].delete();
				}
			}
			monitor.worked(1);
			monitor.subTask("Building the classpath.");
			IPath systemLibs = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
					systemLibs, IRuntimeClasspathEntry.STANDARD_CLASSES);
			systemLibsEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
			IRuntimeClasspathEntry jettyEntry = createRuntimeClasspathEntry("/shindig/jetty-6.1.14.jar");
			IRuntimeClasspathEntry jettyUtilEntry = createRuntimeClasspathEntry("/shindig/jetty-util-6.1.14.jar");
			IRuntimeClasspathEntry servletApiEntry = createRuntimeClasspathEntry("/shindig/servlet-api-2.5-6.1.14.jar");
			IRuntimeClasspathEntry launcherEntry = createRuntimeClasspathEntry("/shindig/launcher.jar");
			ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "Apache Shindig");
			List<String> classpath = new ArrayList<String>();
			classpath.add(systemLibsEntry.getMemento());
			classpath.add(jettyEntry.getMemento());
			classpath.add(jettyUtilEntry.getMemento());
			classpath.add(servletApiEntry.getMemento());
			classpath.add(launcherEntry.getMemento());
			monitor.worked(1);
			monitor.subTask("Creating the launch configuration.");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "Main");
			String warFile = getBundleEntryUrl("/shindig/shindig-server-1.1-SNAPSHOT.war").toExternalForm();
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					"8080 \"" + warFile + "\" \"" + Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.JETTY_DIR) + "\"");
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
