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
 * specific language governing permissions and limitations under
 * the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.runtime.production;

import java.io.File;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * The Action for processing preview a gadget against iGoogle server.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleAction
        implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {
    private static Logger logger = Logger.getLogger(PreviewIGoogleAction.class.getName());

    private IFile gadgetXmlIFile;
    private IWorkbenchPart targetPart;
    private Shell shell;

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbenchWindow window) {
        logger.fine("in init");
        targetPart = window.getActivePage().getActivePart();
        shell = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        logger.fine("in selectionChanged");
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            Object element = structured.getFirstElement();
            if (element instanceof IFile) {
                gadgetXmlIFile = (IFile) element;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        logger.fine("in setActivePart");
        this.targetPart = targetPart;
        shell = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        logger.fine("in run");
        PreviewIGoogleDialog dialog = new PreviewIGoogleDialog(shell);
        logger.fine("dialog: " + dialog);
        int openResult = dialog.open();
        logger.fine("openResult: " + openResult);
        if (openResult == Window.OK) {
            logger.fine("OK pressed");
            String username = dialog.getUsername();
            String password = dialog.getPassword();
            File gadgetXmlFile = gadgetXmlIFile.getRawLocation().toFile();
            Job job = new PreviewIGoogleJob("Preview iGoogle gadget", username,
                    password, dialog.isUseExternalBrowser(), gadgetXmlFile);
            logger.fine("job: " + job);
            job.schedule(1000);
        }
        logger.fine("leaving run");
    }

    public void dispose() {
        logger.fine("in dispose");
    }
}
