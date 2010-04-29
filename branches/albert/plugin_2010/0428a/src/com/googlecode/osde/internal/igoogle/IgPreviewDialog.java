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
package com.googlecode.osde.internal.igoogle;

import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The popup dialog asking for users authentication info in order
 * for previewing gadget on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgPreviewDialog extends IgCredentialsDialog {
    private static Logger logger = new Logger(IgPreviewDialog.class);

    private boolean useCanvasView;
    private Button useCanvasViewButton;
    private boolean useExternalBrowser;
    private Button useExternalBrowserCheckbox;

    public IgPreviewDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.fine("createDialogArea");

        // Set title and message.
        setTitle("Preview Gadget on iGoogle");
        setMessage("The allows you to preview a gadget on iGoogle.");

        // Disable help button.
        setHelpAvailable(false);

        // Prepare dialog area composite.
        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
        prepareComposite(dialogAreaComposite);

        // Prepare panel composite.
        Composite panelComposite = new Composite(dialogAreaComposite, SWT.NONE);
        panelComposite.setLayout(new GridLayout(3, true));
        panelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare radio buttons for choosing view type.
        Label viewTypeLabel = new Label(panelComposite, SWT.LEFT);
        viewTypeLabel.setText("View type: ");
        viewTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        useCanvasViewButton = new Button(panelComposite, SWT.RADIO);
        useCanvasViewButton.setText("Canvas");
        useCanvasViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        useCanvasViewButton.setSelection(true); // default view is canvas view
        Button homeViewButton = new Button(panelComposite, SWT.RADIO);
        homeViewButton.setText("Home");
        homeViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        homeViewButton.setSelection(false); // default view is canvas view

        // Prepare checkbox of useExternalBrowser.
        useExternalBrowserCheckbox = new Button(panelComposite, SWT.CHECK);
        useExternalBrowserCheckbox.setText("Use an external Web browser");
        useExternalBrowserCheckbox.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
        if (!browserSupport.isInternalWebBrowserAvailable()) {
            useExternalBrowserCheckbox.setSelection(true);
            useExternalBrowserCheckbox.setEnabled(false);
        }

        // Prepare extra description.
        Label extraDescription = new Label(panelComposite, SWT.LEFT);
        extraDescription.setText("\nNOTE: Some social-related functions might not work here\n"
                + "unless you check some options via the OSDE feature:\n"
                + "iGoogle - Add Gadget.");
        extraDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        return dialogAreaComposite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(350, 400);
    }

    @Override
    protected void okPressed() {
        useCanvasView = useCanvasViewButton.getSelection();
        useExternalBrowser = useExternalBrowserCheckbox.getSelection();
        super.okPressed();
    }

    boolean isUseCanvasView() {
        return useCanvasView;
    }

    boolean isUseExternalBrowser() {
        return useExternalBrowser;
    }
}
