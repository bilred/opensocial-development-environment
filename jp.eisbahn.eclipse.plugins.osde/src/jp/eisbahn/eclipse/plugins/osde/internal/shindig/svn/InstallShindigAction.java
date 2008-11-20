package jp.eisbahn.eclipse.plugins.osde.internal.shindig.svn;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.tmatesoft.svn.core.SVNException;

public class InstallShindigAction implements IObjectActionDelegate {

	private Shell shell;
	
	private IProject project;
	
	/**
	 * Constructor for Action1.
	 */
	public InstallShindigAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Job job = new Job("Install Apache Shindig") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// shindigディレクトリの作成
					monitor.beginTask("Making the target directory for Apache Shindig.", 1);
					IFolder folder = project.getFolder(new Path("shindig"));
					if (folder.exists()) {
						folder.delete(false, monitor);
					}
					folder.create(false, true, monitor);
					IPath location = folder.getLocation();
					if (location != null) {
						File targetDirectory = location.toFile();
						monitor.done();
						// Expoterに処理を依頼
						ShindigExporter exporter = new ShindigExporter();
						exporter.export(targetDirectory, monitor);
						// リフレッシュ
						folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					} else {
						// TODO ロケーションが得られなかったときの処理
						throw new IllegalStateException("location is null");
					}
					monitor.done();
					return Status.OK_STATUS;
				} catch(CoreException e) {
					return e.getStatus();
				} catch (SVNException e) {
					// TODO 例外処理
					throw new IllegalStateException(e);
				}
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		project = null;
		if (selection != null && !selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structured = (IStructuredSelection)selection;
				Object element = structured.getFirstElement();
				if (element instanceof IProject) {
					project = (IProject)element;
				}
			}
		}
	}

}
