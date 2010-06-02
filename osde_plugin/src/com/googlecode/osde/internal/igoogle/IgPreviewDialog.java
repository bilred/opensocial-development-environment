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

        // Prepare extra description.
        Label extraDescription = new Label(panelComposite, SWT.LEFT);
        extraDescription.setText("\nNotes:\n"
                + "#1 It might take around 5 min for your latest changes to\n"
                + "    take effect.\n"
                + "#2 Some social features will not function properly unless\n"
                + "    you select the necessary options in the 'Add gadget to my\n"
                + "    iGoogle page' step. These options are:\n"
                + "\t(1) Know who I am and see my Friends group\n"
                + "\t(2) Post my activities to Updates\n");
        extraDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        return dialogAreaComposite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(350, 450);
    }

    @Override
    protected void okPressed() {
        useCanvasView = useCanvasViewButton.getSelection();
        super.okPressed();
    }

    boolean isUseCanvasView() {
        return useCanvasView;
    }
}
