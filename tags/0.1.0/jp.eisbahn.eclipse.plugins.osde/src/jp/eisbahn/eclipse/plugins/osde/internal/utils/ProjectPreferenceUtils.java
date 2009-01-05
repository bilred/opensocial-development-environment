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

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectPreferenceUtils {
	
	private static final String LOCAL_WEB_SERVER_PORT = "localWebServerPort";
	
	public static int getLocalWebServerPort(IProject project) throws CoreException {
		String localWebServerPort = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT));
		if (localWebServerPort == null || localWebServerPort.length() == 0) {
			return 8081;
		} else {
			try {
				return Integer.parseInt(localWebServerPort);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return 8081;
			}
		}
	}
	
	public static void setLocalWebServerPort(IProject project, int port) throws CoreException {
		project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT), String.valueOf(port));
		// TODO Update the configuration about the launcing local web server.
		// TODO Update the places written about port number.
	}

}
