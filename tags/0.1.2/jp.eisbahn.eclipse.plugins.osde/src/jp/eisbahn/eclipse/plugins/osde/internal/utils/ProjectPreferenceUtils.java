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
package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.runtime.WebServerLaunchConfigurationCreator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectPreferenceUtils {
	
	private static final String LOCAL_WEB_SERVER_PORT = "localWebServerPort";
	
	private static final int DEFAULT_PORT = 8081;
	
	public static int getLocalWebServerPort(IProject project) throws CoreException {
		String localWebServerPort = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT));
		if (localWebServerPort == null || localWebServerPort.length() == 0) {
			return DEFAULT_PORT;
		} else {
			try {
				return Integer.parseInt(localWebServerPort);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return DEFAULT_PORT;
			}
		}
	}
	
	public static void setLocalWebServerPort(IProject project, int port) throws CoreException {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT), String.valueOf(port));
			WebServerLaunchConfigurationCreator creator = new WebServerLaunchConfigurationCreator();
			creator.create(project, port, new NullProgressMonitor());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}
	}
	
	public static int setDefaultLocalWebServerPort(IProject project) throws CoreException {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT), String.valueOf(DEFAULT_PORT));
			WebServerLaunchConfigurationCreator creator = new WebServerLaunchConfigurationCreator();
			creator.create(project, DEFAULT_PORT, new NullProgressMonitor());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}
		return DEFAULT_PORT;
	}

}
