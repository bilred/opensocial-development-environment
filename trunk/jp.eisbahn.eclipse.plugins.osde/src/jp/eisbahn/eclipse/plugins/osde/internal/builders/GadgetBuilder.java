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
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

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
			// 完全ビルド
			fullBuild(project, monitor);
		} else {
			IResourceDelta delta = getDelta(project);
			if (delta != null) {
				// インクリメンタルビルド
				incrementalBuild(project, delta, monitor);
			} else {
				// 完全ビルド
				fullBuild(project, monitor);
			}
		}
		return new IProject[] {project};
	}

	private void incrementalBuild(IProject project, IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// TODO とりあえずフルビルド
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
										int port = ProjectPreferenceUtils.getLocalWebServerPort(project);
										Pattern pattern = Pattern.compile("http://localhost:" + port + "/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+\\.js");
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
