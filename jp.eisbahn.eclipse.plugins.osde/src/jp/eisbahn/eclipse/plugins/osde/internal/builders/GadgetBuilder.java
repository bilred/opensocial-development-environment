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
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

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

import com.google.gadgets.Module;
import com.google.gadgets.ViewType;
import com.google.gadgets.Module.Content;

public class GadgetBuilder extends IncrementalProjectBuilder {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.gadgetBuilder";

	public GadgetBuilder() {
		super();
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		if (kind == FULL_BUILD) {
			fullBuild(project, monitor);
		} else {
			IResourceDelta delta = getDelta(project);
			if (delta != null) {
				incrementalBuild(project, delta, monitor);
			} else {
				fullBuild(project, monitor);
			}
		}
		return new IProject[] {project};
	}

	private void incrementalBuild(IProject project, IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		fullBuild(project, monitor);
	}

	private void fullBuild(final IProject project, final IProgressMonitor monitor) throws CoreException {
		final IFolder targetDirectory = project.getFolder(new Path("target"));
		if (targetDirectory.exists()) {
			targetDirectory.delete(false, monitor);
		}
		targetDirectory.create(false, true, monitor);
		targetDirectory.setDerived(true);
		project.accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				int type = resource.getType();
				switch(type) {
				case IResource.FILE:
					IFile orgFile = (IFile)resource;
					if (!orgFile.getName().equals(".project")) {
						IPath parent = orgFile.getParent().getProjectRelativePath();
						IFolder destFolder = project.getFolder(targetDirectory.getProjectRelativePath() + "/" + parent);
						IFile destFile = destFolder.getFile(orgFile.getName());
						orgFile.copy(destFile.getFullPath(), false, monitor);
						if (OpenSocialUtil.isGadgetXml(destFile)) {
							try {
								JAXBContext context = JAXBContext.newInstance(Module.class);
								Unmarshaller um = context.createUnmarshaller();
								Module module = (Module)um.unmarshal(orgFile.getContents());
								List<Content> contents = module.getContent();
								Random rnd = new Random();
								for (Content content : contents) {
									if (ViewType.html.toString().equals(content.getType())) {
										String value = content.getValue();
										Pattern pattern = Pattern.compile("http://localhost:8080/" + project.getName() + "/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+\\.js");
										Matcher matcher = pattern.matcher(value);
										StringBuffer sb = new StringBuffer();
										while(matcher.find()) {
											matcher.appendReplacement(sb,
													value.substring(matcher.start(), matcher.end()) + "?rnd=" + Math.abs(rnd.nextInt()));
										}
										matcher.appendTail(sb);
										content.setValue(sb.toString());
									}
								}
								Marshaller ma = context.createMarshaller();
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								ma.marshal(module, out);
								ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
								destFile.setContents(in, true, false, monitor);
							} catch(JAXBException e) {
							} catch(CoreException e) {
							}
						}
					}
					return false;
				case IResource.FOLDER:
					if (resource.isDerived()) {
						return false;
					} else {
						IFolder orgFolder = (IFolder)resource;
						IFolder newFolder = targetDirectory.getFolder(orgFolder.getProjectRelativePath());
						newFolder.create(false, true, monitor);
						newFolder.setDerived(true);
						return true;
					}
				default:
					return true;
				}
			}
		});
	}
	
}
