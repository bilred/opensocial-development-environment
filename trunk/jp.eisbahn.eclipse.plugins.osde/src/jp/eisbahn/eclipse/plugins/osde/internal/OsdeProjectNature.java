package jp.eisbahn.eclipse.plugins.osde.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class OsdeProjectNature implements IProjectNature {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.osdeNature";
	
	private IProject project;

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
