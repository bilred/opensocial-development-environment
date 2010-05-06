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

package com.googlecode.osde.internal.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * An application running outside Eclipse IDE's memory space.
 *
 * @author Dolphin Wan
 */
public class ExternalApp {
    protected final String configurationName;

    /**
     * Creates an instance, without touching the Eclipse environment.
     * Only when calling other methods would trigger interactions with
     * Eclipse.
     *
     * @param configurationName launch configuration name used by this app
     */
    protected ExternalApp(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Terminates running application instance, if any.
     *
     * @throws ExternalAppException if failed to terminate the application.
     */
    public void stop() throws ExternalAppException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunch[] launches = manager.getLaunches();
        for (ILaunch launch : launches) {
            String name = launch.getLaunchConfiguration().getName();
            if (name.equals(configurationName)) {
                try {
                    launch.terminate();
                } catch (DebugException e) {
                    throw new ExternalAppException(e);
                }
                break;
            }
        }
    }

    /**
     * Launches this application using its existing configuration. Nothing
     * happens if no configuration is found.
     *
     * @throws ExternalAppException if failed to launch the application
     */
    public void start() throws ExternalAppException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager.getLaunchConfigurationType(
                IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        try {
            ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);

            for (ILaunchConfiguration configuration : configurations) {
                if (configuration.getName().equals(configurationName)) {
                    final ILaunchConfigurationWorkingCopy wc =
                            configuration.getWorkingCopy();
                    DebugUITools.launch(wc.doSave(), ILaunchManager.RUN_MODE);
                    break;
                }
            }
        } catch (CoreException e) {
            throw new ExternalAppException(e);
        }
    }
}
