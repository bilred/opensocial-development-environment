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
package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ShutoffShindigAction extends Action implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {

    private static final Logger logger = new Logger(ShutoffShindigAction.class);
	private Shell shell;
	private IWorkbenchPart targetPart;
	
	/**
	 * Constructor for Action1.
	 */
	public ShutoffShindigAction() {
		super();
	}
	
	public ShutoffShindigAction(IWorkbenchPart targetPart) {
		super();
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		run(null);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Activator.getDefault().setRunningShindig(false);
		Job job = new Job("Shutting off Apache Shindig.") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Shutting off Apache Shindig.", 7);
				try {
					ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
					ILaunch[] launches = manager.getLaunches();
					for (ILaunch launch : launches) {
						String name = launch.getLaunchConfiguration().getName();
						if (name.equals("Shindig Database") || name.equals("Apache Shindig")) {
							launch.terminate();
							monitor.worked(1);
						}
					}
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							Activator.getDefault().disconnect(targetPart.getSite().getWorkbenchWindow(), monitor);
						}
					});
				} catch (CoreException e) {
					logger.error("To shutdown Apache Shindig or Shindig Database failed.", e);
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		targetPart = window.getActivePage().getActivePart();
		shell = targetPart.getSite().getShell();
	}

}
