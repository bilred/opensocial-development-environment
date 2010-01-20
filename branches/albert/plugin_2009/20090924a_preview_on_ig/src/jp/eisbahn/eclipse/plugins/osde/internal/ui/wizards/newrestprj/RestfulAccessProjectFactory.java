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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newrestprj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.apache.commons.io.IOUtils;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

class RestfulAccessProjectFactory implements IRunnableWithProgress {
	
	private String srcFolderName;
	private String libFolderName;
	private IProject newProjectHandle;
	private String binFolderName;
	private ApplicationImpl application;
	private Person person;
	private IWorkbench workbench;

	RestfulAccessProjectFactory(String srcFolderName,
			String libFolderName, IProject newProjectHandle,
			String binFolderName, ApplicationImpl application,
			Person person, IWorkbench workbench) {
		this.srcFolderName = srcFolderName;
		this.libFolderName = libFolderName;
		this.newProjectHandle = newProjectHandle;
		this.binFolderName = binFolderName;
		this.application = application;
		this.person = person;
		this.workbench = workbench;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		try {
			newProjectHandle.create(monitor);
			newProjectHandle.open(monitor);
			//
			applyJavaNature(monitor);
			//
			IJavaProject javaProject = JavaCore.create(newProjectHandle);
			Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
			//
			IPath sourcePath = javaProject.getPath().append(srcFolderName);
			IFolder sourceDir = createSourceDirectory();
			//
			IPath outputPath = createOutputDirectory(javaProject);
			//
			IClasspathEntry srcEntry = JavaCore.newSourceEntry(sourcePath, new IPath[] {}, outputPath);
			entries.add(srcEntry);
			//
			IPath libPath = javaProject.getPath().append(libFolderName);
			IFolder libDir = createLibraryDirectory();
			//
			copyLibraries(libDir);
			//
			applyClasspath(monitor, javaProject, entries, libPath, libDir);
			//
			IFolder examplesDir = createExamplesDirectory(sourceDir);
			//
			final IFile sampleFile = createSampleCode(examplesDir);
			//
			workbench.getDisplay().syncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow dw = workbench.getActiveWorkbenchWindow();
					try {
						if (dw != null) {
							IWorkbenchPage page = dw.getActivePage();
							if (page != null) {
								IDE.openEditor(page, sampleFile);
							}
						}
					} catch (PartInitException e) {
						throw new RuntimeException(e);
					}
				}
			});
			//
			monitor.worked(1);
			monitor.done();
		} catch(CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

	private IFile createSampleCode(IFolder examplesDir) throws CoreException {
		try {
			InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/ocl/Sample.tmpl"), "UTF-8");
			StringWriter out = new StringWriter();
			IOUtils.copy(in, out);
			String code = out.toString();
			code = code.replace("$consumer_key$", application.getConsumerKey());
			code = code.replace("$consumer_secret$", application.getConsumerSecret());
			code = code.replace("$viewer_id$", person.getId());
			IFile file = examplesDir.getFile("Sample.java");
			ByteArrayInputStream bytes = new ByteArrayInputStream(code.getBytes("UTF-8"));
			if (!file.exists()) {
				file.create(bytes, true, null);
			}
			return file;
		} catch (UnsupportedEncodingException e) {
			Logging.error("Creating the Java file failed.", e);
			throw new IllegalStateException(e);
		} catch (IOException e) {
			Logging.error("Creating the Java file failed.", e);
			throw new IllegalStateException(e);
		}
		
	}

	private IFolder createExamplesDirectory(IFolder sourceDir)
			throws CoreException {
		IFolder exampleDir = sourceDir.getFolder("examples");
		if (!exampleDir.exists()) {
			exampleDir.create(false, true, null);
		}
		return exampleDir;
	}

	private IFolder createSourceDirectory() throws CoreException {
		IFolder sourceDir = newProjectHandle.getFolder(new Path(srcFolderName));
		if (!sourceDir.exists()) {
			sourceDir.create(false, true, null);
		}
		return sourceDir;
	}

	private void applyClasspath(IProgressMonitor monitor,
			final IJavaProject javaProject, Set<IClasspathEntry> entries,
			IPath libPath, IFolder libDir) throws JavaModelException {
		IClasspathContainer libContainer = new ClasspathContainerImpl(libDir, libPath);
		JavaCore.setClasspathContainer(libPath,
				new IJavaProject[] { javaProject },
				new IClasspathContainer[] { libContainer }, null);
		//
		IClasspathEntry libEntry = JavaCore.newContainerEntry(libPath, null,
				new IClasspathAttribute[] {}, false);
		entries.add(libEntry);
		//
		entries.add(JavaRuntime.getDefaultJREContainerEntry());
		javaProject.setRawClasspath(
				entries.toArray(new IClasspathEntry[entries.size()]), monitor);
	}

	private IFolder createLibraryDirectory() throws CoreException {
		IFolder libDir = newProjectHandle.getFolder(new Path(libFolderName));
		if (!libDir.exists()) {
			libDir.create(false, true, null);
		}
		return libDir;
	}

	private void copyLibraries(IFolder libDir) throws CoreException {
		String[] jars = new String[] {
				"commons-codec-1.3.jar", "core-20081027.jar",
				"javax.servlet_2.4.0.v200806031604.jar", "opensocial-20081218.jar"};
		for (String jar : jars) {
			InputStream in = getClass().getResourceAsStream("/ocl/" + jar);
			IFile jarFile = libDir.getFile(jar);
			jarFile.create(in, true, null);
		}
	}

	private IPath createOutputDirectory(final IJavaProject javaProject)
			throws CoreException {
		IPath outputPath = javaProject.getPath().append(binFolderName);
		IFolder outputDir = newProjectHandle.getFolder(new Path(binFolderName));
		if (!outputDir.exists()) {
			outputDir.create(false, true, null);
		}
		return outputPath;
	}

	private void applyJavaNature(IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = newProjectHandle.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JavaCore.NATURE_ID;
		description.setNatureIds(newNatures);
		newProjectHandle.setDescription(description, monitor);
	} 
	
	private class ClasspathContainerImpl implements IClasspathContainer {
		
		private IFolder libDir;
		private IPath libPath;

		public ClasspathContainerImpl(IFolder libDir, IPath libPath) {
			super();
			this.libDir = libDir;
			this.libPath = libPath;
		}
		
		public IClasspathEntry[] getClasspathEntries() {
			List<IClasspathEntry> ices = new ArrayList<IClasspathEntry>();
			try {
				IResource[] rs = libDir.members();
				for (IResource r : rs) {
					if (r instanceof IFile) {
						IFile f = (IFile) r;
						IClasspathEntry entry = JavaCore.newLibraryEntry(f.getFullPath(), null, null, false);
						ices.add(entry);
					}
				}
			} catch (CoreException e) {
				Logging.error("Adding the classpath entries failed.", e);
			}
			return ices.toArray(new IClasspathEntry[ices.size()]);
		}

		public String getDescription() {
			return "OpenSocial Java Client Library";
		}

		public int getKind() {
			return IClasspathContainer.K_APPLICATION;
		}

		public IPath getPath() {
			return libPath;
		}
	};
}