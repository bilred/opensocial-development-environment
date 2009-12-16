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
package jp.eisbahn.eclipse.plugins.osde.internal.common;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.*;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private String mainClassName;
    private final List<String> programArguments = new ArrayList<String>();

    public JavaLaunchConfigurationBuilder(String configurationName) throws IOException {
        this.configurationName = configurationName;
    }

    /**
     * Include an embedded jar file in the java application's classpath.
     * @param classpath A classpath starting with '/' e.g. /shindig/shindig-1.0.jar. 
     */
    public JavaLaunchConfigurationBuilder withLibrary(String classpath) {
        if (!classpath.startsWith("/")) {
            classpath = "/" + classpath;
        }
        libraries.add(classpath);
        return this;
    }

    /**
     * Append a command-line argument.
     * @param value The returned value of its <code>toString()</code> method will be used.
     */
    public JavaLaunchConfigurationBuilder withArgument(Object value) {
        programArguments.add(String.valueOf(value));
        return this;
    }

    /**
     * Specify the main class to run.
     * @param mainClassName A fully-qualified class name.
     */
    public JavaLaunchConfigurationBuilder withMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
        return this;
    }

    /**
     * Builds a new configuration instance. Note that any existing
     * configurations with the same name will be removed first.
     * @throws CoreException Thrown when Eclipse cannot create a new
     *     configuration.
     * @throws IOException Thrown when specified jar files cannot be found.
     */
    public ILaunchConfiguration build() throws CoreException, IOException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager
                .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

        removeExistingConfiguration(manager, type);

        List<String> classpath = buildClasspath();

        ILaunchConfigurationWorkingCopy wc = createConfiguration(type, classpath);
        return wc.doSave();
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

        ILaunchConfigurationWorkingCopy wc = type.newInstance(null, configurationName);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClassName);
        wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
                argumentsAsString.toString());
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

    private void removeExistingConfiguration(ILaunchManager manager, ILaunchConfigurationType type)
            throws CoreException {
        ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
        for (ILaunchConfiguration configuration : configurations) {
            if (configuration.getName().equals(configurationName)) {
                configuration.delete();
            }
        }
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
