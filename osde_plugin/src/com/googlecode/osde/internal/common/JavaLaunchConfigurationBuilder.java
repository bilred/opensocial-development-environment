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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.osde.internal.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * A helper class to simplify the creation of an Eclipse launch configuration
 * for Java applications. Here is an example:<pre>
 * new JavaLaunchConfigurationBuilder("Run Jetty server")
 *     .withMainClassName("org.jetty.Server");
 *     .withLibrary("/embedded/jetty.jar");
 *     .withLibrary("/embedded/jetty-ext.jar");
 *     .withArgument("-warPath=/usr/mywar.war");
 *     .build()
 * </pre>
 *
 * @author Dolphin Chi-Ngai Wan
 */
public final class JavaLaunchConfigurationBuilder {
    private final String configurationName;
    private final List<String> libraries = new ArrayList<String>();
    private final List<String> programArguments = new ArrayList<String>();
    private String mainClassName;
    private final Map<String, String> vmArguments = new LinkedHashMap<String, String>();

    public JavaLaunchConfigurationBuilder(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Includes an embedded jar file in the java application's classpath.
     *
     * @param classpath a classpath starting with '/' e.g. /a/shindig-1.0.jar
     */
    public JavaLaunchConfigurationBuilder withLibrary(String classpath) {
        if (!classpath.startsWith("/")) {
            classpath = "/" + classpath;
        }
        libraries.add(classpath);
        return this;
    }

    /**
     * Appends a command-line argument with its toString() value.
     */
    public JavaLaunchConfigurationBuilder withArgument(Object value) {
        programArguments.add(String.valueOf(value));
        return this;
    }

    /**
     * Appends a command-line argument and doubled-quotes its toString() value.
     */
    public JavaLaunchConfigurationBuilder withArgumentQuoted(Object value) {
        return withArgument("\"" + String.valueOf(value) + "\"");
    }

    /**
     * Specifies the main class to run.
     *
     * @param mainClassName a fully-qualified class name
     */
    public JavaLaunchConfigurationBuilder withMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
        return this;
    }

    /**
     * Adds a VM argument into the command line.
     *
     * @param name a VM argument name
     * @param value a VM argument value
     */
    public JavaLaunchConfigurationBuilder withVmArgument(String name, String value) {
        this.vmArguments.put(name, value);
        return this;
    }

    /**
     * Builds a new configuration instance. Note that any existing
     * configurations with the same name will be removed first.
     *
     * @throws CoreException if Eclipse cannot create a new configuration
     * @throws IOException if specified jar files cannot be found
     */
    public ILaunchConfiguration build() throws CoreException, IOException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager
                .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

        List<String> classpath = buildClasspath();

        ILaunchConfigurationWorkingCopy wc = createConfiguration(type, classpath);
        return wc.doSave();
    }

    /**
     * Searches and removes any existing Java launch configuration with the
     * given name passed via constructor.
     */
    public JavaLaunchConfigurationBuilder removeExistingConfiguration() throws CoreException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager
                .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
        for (ILaunchConfiguration configuration : configurations) {
            if (configuration.getName().equals(configurationName)) {
                configuration.delete();
            }
        }
        return this;
    }

    private ILaunchConfigurationWorkingCopy createConfiguration(ILaunchConfigurationType type,
            List<String> classpath)
            throws CoreException {
        StringBuilder argumentsAsString = new StringBuilder();
        for (String argument : programArguments) {
            if (argumentsAsString.length() > 0) {
                argumentsAsString.append(' ');
            }
            argumentsAsString.append(argument);
        }

        StringBuilder vmArgumentsAsString = new StringBuilder();
        for (Map.Entry<String, String> entry : vmArguments.entrySet()) {
            vmArgumentsAsString.append(" -D").append(entry.getKey())
                    .append("=\"").append(entry.getValue()).append("\"");
        }

        ILaunchConfigurationWorkingCopy wc = type.newInstance(null, configurationName);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClassName);

        if (argumentsAsString.length() > 0) {
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                    argumentsAsString.toString());
        }

        if (vmArgumentsAsString.length() > 0) {
            wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
                    vmArgumentsAsString.toString());
        }
        return wc;
    }

    private List<String> buildClasspath() throws CoreException, IOException {
        List<String> classpath = new ArrayList<String>();

        IRuntimeClasspathEntry systemLibsEntry = createJreEntry();
        classpath.add(systemLibsEntry.getMemento());

        for (String library : libraries) {
            IRuntimeClasspathEntry entry = createJarEntry(library);
            classpath.add(entry.getMemento());
        }
        return classpath;
    }

    private IRuntimeClasspathEntry createJreEntry() throws CoreException {
        IPath systemLibs = new Path(JavaRuntime.JRE_CONTAINER);
        IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
                systemLibs, IRuntimeClasspathEntry.STANDARD_CLASSES);
        systemLibsEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
        return systemLibsEntry;
    }

    private IRuntimeClasspathEntry createJarEntry(String path) throws IOException {
        URL url = getBundleEntryUrl(path);
        IRuntimeClasspathEntry entry =
                JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(url.getPath()));
        entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
        return entry;
    }

    private URL getBundleEntryUrl(String path) throws IOException {
        return FileLocator.toFileURL(new URL(Activator.getDefault().getBundle().getEntry(path)
                .toExternalForm()));
    }
}
