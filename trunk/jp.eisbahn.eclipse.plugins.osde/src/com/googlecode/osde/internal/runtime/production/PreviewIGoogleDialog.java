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
package com.googlecode.osde.internal.runtime.production;

import java.util.logging.Logger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The popup dialog asking for users authentication info in order
 * for previewing gadget on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleDialog extends TitleAreaDialog {
    private static Logger logger = Logger.getLogger(PreviewIGoogleDialog.class.getName());

    private String username;
    private String password;
    private boolean useCanvasView;
    private boolean useExternalBrowser;
    private Text usernameText;
    private Text passwordText;
    private Button canvasViewButton;
    private Button useExternalBrowserCheckbox;

    public PreviewIGoogleDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.fine("createDialogArea");

        // Set title and message.
        setTitle("Preview Gadget on iGoogle");
        setMessage("Please enter your gmail account info.");

        // Prepare composite and panel.
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, true);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare username.
        Label usernameLabel = new Label(panel, SWT.LEFT);
        usernameLabel.setText("Username: ");
        usernameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        usernameText = new Text(panel, SWT.SINGLE);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare password.
        Label passwordLabel = new Label(panel, SWT.LEFT);
        passwordLabel.setText("Password: ");
        passwordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        passwordText = new Text(panel, SWT.SINGLE);
        passwordText.setEchoChar('*');
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare radio buttons for choosing view type.
        Label viewTypeLabel = new Label(panel, SWT.LEFT);
        viewTypeLabel.setText("View type: ");
        viewTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        Button homeViewButton = new Button(panel, SWT.RADIO);
        homeViewButton.setText("Home");
        homeViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        homeViewButton.setSelection(true); // default view is home view
        canvasViewButton = new Button(panel, SWT.RADIO);
        canvasViewButton.setText("Canvas");
        canvasViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        // Prepare checkbox of useExternalBrowser.
        useExternalBrowserCheckbox = new Button(panel, SWT.CHECK);
        useExternalBrowserCheckbox.setText("Use an external Web browser.");
        useExternalBrowserCheckbox.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
        if (!browserSupport.isInternalWebBrowserAvailable()) {
            useExternalBrowserCheckbox.setSelection(true);
            useExternalBrowserCheckbox.setEnabled(false);
        }

        return composite;
    }

    @Override
    protected Point getInitialSize() {
        logger.fine("getInitialSize");
        return new Point(350, 300);
    }

    @Override
    protected void okPressed() {
        logger.fine("okPressed");

        username = usernameText.getText();
        logger.fine("username: " + username);
        password = passwordText.getText();
        logger.fine("password: " + password);
        useCanvasView = canvasViewButton.getSelection();
        useExternalBrowser = useExternalBrowserCheckbox.getSelection();
        logger.fine("useExternalBrowser: " + useExternalBrowser);

        logger.fine("gonna setReturnCode");
        setReturnCode(OK);
        logger.fine("gonna close");
        close();
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    boolean isUseCanvasView() {
        return useCanvasView;
    }

    boolean isUseExternalBrowser() {
        return useExternalBrowser;
    }
}
