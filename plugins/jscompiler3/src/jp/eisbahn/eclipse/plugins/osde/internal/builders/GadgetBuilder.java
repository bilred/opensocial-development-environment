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
package jp.eisbahn.eclipse.plugins.osde.internal.builders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.jscompiler.JavaScriptCompilerRunner;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.google.gadgets.GadgetXmlSerializer;
import com.google.gadgets.ViewType;
import com.google.gadgets.model.Module;
import com.google.gadgets.model.Module.Content;
import com.google.gadgets.parser.IParser;
import com.google.gadgets.parser.ParserException;
import com.google.gadgets.parser.ParserFactory;
import com.google.gadgets.parser.ParserType;

/**
 * Gadget builder.
 */
public class GadgetBuilder extends IncrementalProjectBuilder {

    public static final String ID = "jp.eisbahn.eclipse.plugins.osde.gadgetBuilder";

    private static final Logger logger = new Logger(GadgetBuilder.class);

    private Pattern ignoreFolderPattern;

    public GadgetBuilder() {
        super();
        ignoreFolderPattern = Pattern.compile("(\\.svn$)|(\\.git$)|(\\.hg$)|(\\.cvs$)|(\\.bzr$)");
    }

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		if (kind == FULL_BUILD) {
			fullBuild(project, monitor);
		} else {
            // The eclipse platform generates resource delta when we change the
            // contents of the target folder. We should not need to do a full
            // rebuild again for that.
			IResourceDelta delta = getDelta(project);
            if (delta == null || doesNotContainTargetFolder(delta)) {
                fullBuild(project, monitor);
            }
		}
		return new IProject[] {project};
	}

    private boolean doesNotContainTargetFolder(IResourceDelta delta) {
        if (delta == null) {
            return true;
        }

        boolean containsExcludedFolders = false;
        boolean containsMonitoredFolders = false;
        for (IResourceDelta affected : delta.getAffectedChildren()){
            if (affected.getResource().equals(getTargetFolder())) {
                containsExcludedFolders = true;
            } else {
                containsMonitoredFolders = true;
            }
        }

        return !containsExcludedFolders || containsMonitoredFolders;
    }

    private void fullBuild(final IProject project, final IProgressMonitor monitor) throws CoreException {
        final boolean enableJavaScriptCompiler =
                Activator.getDefault().getOsdeConfiguration().isCompileJavaScript();

        final IFolder targetDirectory = getTargetFolder();
        if (targetDirectory.exists()) {
			targetDirectory.delete(false, monitor);
		}
		targetDirectory.create(false, true, monitor);
		targetDirectory.setDerived(true);

        final JavaScriptCompilerRunner runner = new JavaScriptCompilerRunner();

        project.accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
                if (resource.isDerived()) {
                    return false;
                }

				int type = resource.getType();
				switch(type) {
				case IResource.FILE:
					IFile orgFile = (IFile)resource;
					if (!orgFile.getName().equals(".project")) {
						IPath parent = orgFile.getParent().getProjectRelativePath();
						IFolder destFolder = project.getFolder(targetDirectory.getProjectRelativePath() + "/" + parent);
						IFile destFile = destFolder.getFile(orgFile.getName());

                        try {
                            if (OpenSocialUtil.isGadgetSpecXML(orgFile)) {
                                compileGadgetSpec(orgFile, destFile, project, monitor);
                            } else if (enableJavaScriptCompiler && isJavaScript(orgFile)) {
                                compileJavaScript(orgFile, destFile, runner);
                            } else {
                                orgFile.copy(destFile.getFullPath(),
                                        IResource.FORCE | IResource.DERIVED, monitor);
                            }
                        } catch (IOException e) {
                            logger.warn("Failed to copy a file to the target folder.", e);
                        } catch (ParserException e) {
                            logger.warn("Failed to build the gadget spec XML file.", e);
                        }
                    }
					return false;
				case IResource.FOLDER:
						IFolder orgFolder = (IFolder)resource;
						if (shouldCopyFolder(orgFolder)) {
							IFolder newFolder = targetDirectory.getFolder(orgFolder.getProjectRelativePath());
							newFolder.create(false, true, monitor);
							newFolder.setDerived(true);
							return true;
						} else {
							return false;
						}
				default:
					return true;
				}
			}
        }, IResource.DEPTH_INFINITE, IContainer.EXCLUDE_DERIVED);

        // Compile all JavaScript files.
        runner.schedule();
    }

    private IFolder getTargetFolder() {
        return getProject().getFolder(new Path("target"));
    }

    private boolean isJavaScript(IFile orgFile) {
        return "js".equalsIgnoreCase(orgFile.getFileExtension());
    }

    private void compileJavaScript(IFile source, IFile target, JavaScriptCompilerRunner runner) {
        runner.addFile(source, target);
    }

    private void compileGadgetSpec(IFile source, IFile target, IProject project,
            IProgressMonitor monitor)
            throws CoreException, ParserException, UnsupportedEncodingException {
        IParser gadgetXMLParser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);

        InputStream fileContent = source.getContents();
        Module module;
        try {
            module = (Module) gadgetXMLParser.parse(fileContent);
        } finally {
            IOUtils.closeQuietly(fileContent);
        }

        List<Content> contents = module.getContent();
        Random rnd = new Random();
        for (Content content : contents) {
            if (ViewType.html.toString().equals(content.getType())) {
                String value = content.getValue();
                Pattern pattern = Pattern.compile("http://localhost:[0-9]+/" + project.getName() +
                        "/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+\\.js");
                Matcher matcher = pattern.matcher(value);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb,
                            value.substring(matcher.start(), matcher.end()) + "?rnd=" +
                                    Math.abs(rnd.nextInt()));
                }
                matcher.appendTail(sb);
                content.setValue(sb.toString());
            }
        }
        String serialized = GadgetXmlSerializer.serialize(module);
        ByteArrayInputStream content = new ByteArrayInputStream(serialized.getBytes("UTF-8"));
        target.create(content, IResource.DERIVED | IResource.FORCE, monitor);
    }

    protected boolean shouldCopyFolder(IFolder folder) {
        String name = folder.getName();
        Matcher matcher = ignoreFolderPattern.matcher(name);
        return !matcher.matches();
    }

}
