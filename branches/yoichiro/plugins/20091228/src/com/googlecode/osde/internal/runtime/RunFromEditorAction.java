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
package com.googlecode.osde.internal.runtime;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.editors.GadgetXmlEditor;
import com.googlecode.osde.internal.shindig.ShindigLauncher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Action available in a gadget spec XML editor to run the gadget
 * with a local Shindig server.
 */
public class RunFromEditorAction extends AbstractRunAction implements IEditorActionDelegate {

	private IEditorPart targetEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
		shell = targetEditor != null ? targetEditor.getSite().getShell() : null;
		targetPart = targetEditor;
	}

	public void run(IAction action) {
		if (targetEditor instanceof GadgetXmlEditor) {
			GadgetXmlEditor editor = (GadgetXmlEditor)targetEditor;
			final IFile gadgetXmlFile = (IFile)editor.getEditorInput().getAdapter(IResource.class);
			final IProject project = gadgetXmlFile.getProject();
			if (!Activator.getDefault().isRunningShindig()) {
				boolean result = ShindigLauncher.launchWithConfirm(shell, targetPart);
				if (result) {
					shell.getDisplay().timerExec(10000, new Runnable() {
						public void run() {
							launch(gadgetXmlFile, project);
						}
					});
					return;
				}
			} else {
				launch(gadgetXmlFile, project);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
