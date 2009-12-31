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

package com.googlecode.osde.internal.jscompiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.osde.internal.common.AbstractJob;
import com.googlecode.osde.internal.common.JavaLaunchConfigurationBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * Invokes a JavaScript compiler as a new process.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class JavaScriptCompilerRunner extends AbstractJob {

    private final JavaScriptCompilerReporter reporter;
    private final List<CompilationUnit> units;

    public JavaScriptCompilerRunner() {
        super("Compiles all JavaScript files");
        this.units = new ArrayList<CompilationUnit>();
        this.reporter = new JavaScriptCompilerReporter();
    }

    /**
     * Queues a JavaScript file for future compilation when {@link #schedule()}
     * is called. 
     */
    public void addFile(IFile sourceFile, IFile targetFile) {
        units.add(new CompilationUnit(sourceFile, targetFile));
    }

    @Override
    protected void runImpl(final IProgressMonitor monitor) throws Exception {
        int count = units.size();
        if (count == 0) {
            return;
        }

        monitor.beginTask(getName(), count);

        for (CompilationUnit unit : units) {
            final String sourceFilePath = pathOf(unit.sourceFile);

            monitor.subTask("Compiling " + sourceFilePath);
            ILaunchConfiguration configuration =
                    new JavaLaunchConfigurationBuilder(
                            "Compile JavaScript " + unit.sourceFile.getName())
                            .withMainClassName("com.google.javascript.jscomp.CompilerRunner")
                            .withLibrary("/jscompiler/compiler.jar")
                            .withArgument("--js=" + sourceFilePath)
                            .build();

            final ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, monitor, false);
            unit.startMonitoring(launch, monitor);

            if (monitor.isCanceled()) {
                // Stop further compilation e.g. when project is closing.
                return;
            }
        }
    }

    private static String pathOf(IFile file) {
        return file.getLocation().toOSString();
    }

    /**
     * A compilation responsible to monitor the compiler process, and
     * collect its stdout/stderr output when it exits.
     */
    class CompilationUnit implements ILaunchesListener2 {

        private static final String ENCODING = "UTF-8";

        final IFile sourceFile;
        final IFile targetFile;
        ILaunch launch;
        IProgressMonitor monitor;
        ILaunchManager manager;

        CompilationUnit(IFile sourceFile, IFile targetFile) {
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
        }

        void startMonitoring(ILaunch launch, IProgressMonitor monitor) {
            this.launch = launch;
            this.launch.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, "true");
            this.launch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, ENCODING);

            IStreamsProxy streamsProxy = launch.getProcesses()[0].getStreamsProxy();
            enableBuffering(streamsProxy.getOutputStreamMonitor());
            enableBuffering(streamsProxy.getErrorStreamMonitor());

            this.monitor = monitor;
            this.manager = DebugPlugin.getDefault().getLaunchManager();
            this.manager.addLaunchListener(this);
        }

        void enableBuffering(IStreamMonitor monitor) {
            if (monitor instanceof IFlushableStreamMonitor) {
                ((IFlushableStreamMonitor) monitor).setBuffered(true);
            }
        }

        /**
         * When the compiler terminates, we write back its stdout to
         * target file.
         */
        public void launchesTerminated(ILaunch[] launches) {
            for (ILaunch terminated : launches) {
                if (!launch.equals(terminated)) {
                    continue;
                }

                final String sourceFilePath = pathOf(sourceFile);

                IProcess process = terminated.getProcesses()[0];
                try {
                    reporter.clear(sourceFile);

                    targetFile.create(getStdout(process), IResource.FORCE | IResource.DERIVED,
                            monitor);

                    if (process.getExitValue() != 0) {
                        String warningOrErrors =
                                process.getStreamsProxy().getErrorStreamMonitor().getContents();
                        reporter.reportErrorOrWarning(sourceFile, warningOrErrors);
                    }
                } catch (CoreException e) {
                    logger.error("Cannot write to target file " + pathOf(targetFile));
                } catch (UnsupportedEncodingException e) {
                    try {
                        reporter.reportErrorOrWarning(sourceFile, "Containing non-UTF-8 characters");
                    } catch (CoreException e1) {
                        logger.error("Cannot report errors to " + sourceFilePath, e1);
                    }
                } finally {
                    manager.removeLaunchListener(this);

                    final ILaunchConfiguration configuration = launch.getLaunchConfiguration();
                    if (configuration != null) {
                        try {
                            configuration.delete();
                        } catch (CoreException e) {
                            logger.error("Cannot remove compilation configuration of " +
                                    sourceFilePath, e);
                        }
                    }

                    monitor.worked(1);
                }
            }
        }

        private InputStream getStdout(IProcess process) throws UnsupportedEncodingException {
            String output =
                    process.getStreamsProxy().getOutputStreamMonitor().getContents();
            return new ByteArrayInputStream(output.getBytes(ENCODING));
        }

        public void launchesRemoved(ILaunch[] iLaunches) {
            // event not interested
        }

        public void launchesAdded(ILaunch[] iLaunches) {
            // event not interested
        }

        public void launchesChanged(ILaunch[] iLaunches) {
            // event not interested
        }
    }
}