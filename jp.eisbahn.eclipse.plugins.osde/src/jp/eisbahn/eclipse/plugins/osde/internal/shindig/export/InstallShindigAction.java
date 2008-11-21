package jp.eisbahn.eclipse.plugins.osde.internal.shindig.export;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
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
					// Maven2の入手
					monitor.beginTask("Making the target directory for Apache Maven2.", 1);
					IFolder maven2Folder = project.getFolder(new Path("maven"));
					if (maven2Folder.exists()) {
						maven2Folder.delete(false, monitor);
					}
					maven2Folder.create(false, true, monitor);
					IPath maven2Location = maven2Folder.getLocation();
					if (maven2Location != null) {
						File maven2TargetDirectory = maven2Location.toFile();
						Maven2Exporter maven2Exporter = new Maven2Exporter();
						maven2Exporter.export(maven2TargetDirectory, monitor);
						// リフレッシュ
						maven2Folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					} else {
						// TODO ロケーションが得られなかったときの処理
						throw new IllegalStateException("location is null");
					}
					// shindigディレクトリの作成
					monitor.beginTask("Making the target directory for Apache Shindig.", 1);
					IFolder shindigFolder = project.getFolder(new Path("shindig"));
					if (shindigFolder.exists()) {
						shindigFolder.delete(false, monitor);
					}
					shindigFolder.create(false, true, monitor);
					IPath shindigLocation = shindigFolder.getLocation();
					if (shindigLocation != null) {
						File shindigTargetDirectory = shindigLocation.toFile();
						monitor.done();
						// ExporterにShindig入手処理を依頼
						ShindigExporter shindigExporter = new ShindigExporter();
						shindigExporter.export(shindigTargetDirectory, monitor);
						// リフレッシュ
						shindigFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
				} catch (HttpException e) {
					// TODO 例外処理
					throw new IllegalStateException(e);
				} catch (IOException e) {
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
