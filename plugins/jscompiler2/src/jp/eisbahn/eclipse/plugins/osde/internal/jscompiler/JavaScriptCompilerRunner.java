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

package jp.eisbahn.eclipse.plugins.osde.internal.jscompiler;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.common.BaseJob;
import jp.eisbahn.eclipse.plugins.osde.internal.common.JavaLaunchConfigurationBuilder;

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
public class JavaScriptCompilerRunner extends BaseJob {

    private final List<CompilationUnit> units;

    public JavaScriptCompilerRunner() {
        super("Compiles all JavaScript files");
        this.units = new ArrayList<CompilationUnit>();
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
            final String sourceFilePath = unit.sourceFilePath;

            monitor.subTask("Compiling " + sourceFilePath);
            ILaunchConfiguration configuration =
                    new JavaLaunchConfigurationBuilder("JavaScript Compiler")
                            .withMainClassName("com.google.javascript.jscomp.CompilerRunner")
                            .withLibrary("/jscompiler/compiler.jar")
                            .withArgument("--js=" + sourceFilePath)
                            .build();

            final ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, monitor, false);
            unit.startMonitoring(launch, monitor);
            monitor.worked(1);

            if (monitor.isCanceled()) {
                // Stop further compilation e.g. when project is closing.
                return;
            }
        }
    }

    /**
     * A compilation responsible to monitor the compiler process, and
     * collect its stdout/stderr output when it exits.
     */
    class CompilationUnit implements ILaunchesListener2 {

        private static final String ENCODING = "UTF-8";

        final String sourceFilePath;
        final IFile targetFile;
        ILaunch launch;
        IProgressMonitor monitor;
        ILaunchManager manager;

        CompilationUnit(IFile sourceFile, IFile targetFile) {
            this.sourceFilePath = sourceFile.getLocation().toOSString();
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

                logger.info("Receiving:" + sourceFilePath);

                IProcess process = terminated.getProcesses()[0];

                try {
                    String output =
                            process.getStreamsProxy().getOutputStreamMonitor().getContents();

                    targetFile.create(new ByteArrayInputStream(output.getBytes(ENCODING)),
                            IResource.FORCE | IResource.DERIVED, monitor);

                    if (process.getExitValue() != 0) {
                        // TODO: direct compiler warnings/errors to 'Problems' view.
                        String stderr =
                                process.getStreamsProxy().getErrorStreamMonitor()
                                        .getContents();
                        logger.error(stderr);
                    }
                } catch (CoreException e) {
                    logger.error("Cannot write to target file " +
                            targetFile.getLocation().toOSString());
                } catch (UnsupportedEncodingException e) {
                    // TODO: direct to 'Problems' view
                    logger.error(sourceFilePath + " contains non-UTF8 characters", e);
                } finally {
                    manager.removeLaunchListener(this);
                    try {
                        launch.getLaunchConfiguration().delete();
                    } catch (CoreException e) {
                        logger.warn("Cannot remove compilation configuration of " +
                                sourceFilePath, e); 
                    }
                }
            }
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
