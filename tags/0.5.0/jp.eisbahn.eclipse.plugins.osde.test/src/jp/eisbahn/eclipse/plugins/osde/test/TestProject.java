package jp.eisbahn.eclipse.plugins.osde.test;

import java.util.Random;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 *
 *
 * @author qrtt1
 */
public class TestProject {

    protected static final NullProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();
    static Random random = new Random();
    final IProject project;
    final IProjectDescription description;
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    public TestProject() throws CoreException {
        this("osde_project_" + random.nextInt());
    }

    public TestProject(String projectName) throws CoreException {
        project = workspace.getRoot().getProject(projectName);
        description = workspace.newProjectDescription(projectName);

        project.create(description, NULL_PROGRESS_MONITOR);
        project.open(NULL_PROGRESS_MONITOR);
    }

    public IProject getProject(){
        return project;
    }

    public void dispose() throws CoreException {
        project.delete(true, NULL_PROGRESS_MONITOR);
    }

}
