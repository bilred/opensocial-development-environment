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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.osde.internal.common.AbstractJob;
import com.googlecode.osde.internal.utils.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Invokes a JavaScript compiler as a new process.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class JavaScriptCompilerRunner extends AbstractJob {

    private static final String MARKER_ID =
            "jp.eisbahn.eclipse.plugins.osde.markers.JsCompileMarker";

    private static final Logger logger = new Logger(JavaScriptCompilerRunner.class);

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
        final int count = units.size();
        if (count == 0) {
            return;
        }

        // Resource change events will be fired after all compilation is done.
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable(){
            public void run(IProgressMonitor iProgressMonitor) throws CoreException {
                monitor.beginTask(getName(), count);

                for (CompilationUnit unit : units) {
                    unit.run(monitor);

                    // Stop further compilation e.g. when project is closing.
                    if (monitor.isCanceled()) {
                        break;
                    }
                }

                units.clear();
            }
        }, monitor);
    }

    private static String pathOf(IFile file) {
        return file.getLocation().toOSString();
    }

    /**
     * A compilation responsible to monitor the compiler process, and collect
     * its stdout/stderr output when it exits.
     */
    class CompilationUnit implements JavaScriptCompiler.Reporter {

        final IFile sourceFile;
        final IFile targetFile;

        CompilationUnit(IFile sourceFile, IFile targetFile) {
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
        }

        /**
         * After the compiler runs, we write back its stdout to target file.
         */
        public void run(IProgressMonitor monitor) {
            final String sourceFilePath = pathOf(sourceFile);
            final String targetFilePath = pathOf(targetFile);

            monitor.subTask("Compiling " + sourceFilePath);
            try {
                clearMarkers();

                JavaScriptCompiler compiler = new ClosureCompiler(
                        sourceFilePath, targetFilePath, "UTF-8", this);

                InputStream compiledSource = compiler.compile();

                targetFile.create(compiledSource, IResource.FORCE | IResource.DERIVED, monitor);
            } catch (CoreException e) {
                logger.error("Failed to write to target file " + targetFilePath);
            } catch (IOException e) {
                logger.error("Failed to compile source file " + sourceFilePath);
            } finally {
                monitor.worked(1);
            }
        }

        public void reportIssue(boolean isError, String description, int lineNumber) {
            try {
                IMarker marker = sourceFile.createMarker(MARKER_ID);
                marker.setAttribute(IMarker.MESSAGE, description);
                marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
                marker.setAttribute(IMarker.SEVERITY,
                        isError ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
            } catch (CoreException e) {
                logger.warn("Failed to create markers to " + pathOf(sourceFile), e);
            }
        }

        private void clearMarkers() {
            try {
                sourceFile.deleteMarkers(MARKER_ID, false, IResource.DEPTH_ZERO);
            } catch (CoreException e) {
                logger.warn("Failed to delete markers to " + pathOf(sourceFile), e);
            }
        }
    }
}
