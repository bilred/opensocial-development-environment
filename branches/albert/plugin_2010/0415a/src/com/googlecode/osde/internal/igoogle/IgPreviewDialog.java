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
public class IgPreviewDialog extends TitleAreaDialog {
    private static Logger logger = new Logger(IgPreviewDialog.class);

    private String username;
    private String password;
    private String hostProjectName;
    private boolean useCanvasView;
    private boolean useExternalBrowser;
    private Text usernameText;
    private Text passwordText;
    private Text hostProjectNameText;
    private Button canvasViewButton;
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

        // Prepare composite and panel.
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, true);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare the description for google account info.
        Label googleAccountInfo = new Label(panel, SWT.LEFT);
        googleAccountInfo.setText("Google account info");
        googleAccountInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        
        // Prepare username.
        Label usernameLabel = new Label(panel, SWT.LEFT);
        usernameLabel.setText("\tUsername: ");
        usernameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        usernameText = new Text(panel, SWT.SINGLE);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare password.
        Label passwordLabel = new Label(panel, SWT.LEFT);
        passwordLabel.setText("\tPassword: ");
        passwordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        passwordText = new Text(panel, SWT.SINGLE);
        passwordText.setEchoChar('*');
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare host-project-name.
        Label hostProjectNameLabel = new Label(panel, SWT.LEFT);
        hostProjectNameLabel.setText("Project name: ");
        hostProjectNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        hostProjectNameText = new Text(panel, SWT.SINGLE);
        hostProjectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        hostProjectNameText.setText(IgPreviewJob.HOST_PROJECT_NAME_FOR_PREVIEW);

        // Prepare radio buttons for choosing view type.
        Label viewTypeLabel = new Label(panel, SWT.LEFT);
        viewTypeLabel.setText("View type: ");
        viewTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        canvasViewButton = new Button(panel, SWT.RADIO);
        canvasViewButton.setText("Canvas");
        canvasViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        canvasViewButton.setSelection(true); // default view is canvas view
        Button homeViewButton = new Button(panel, SWT.RADIO);
        homeViewButton.setText("Home");
        homeViewButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        homeViewButton.setSelection(false); // default view is canvas view

        // Prepare checkbox of useExternalBrowser.
        useExternalBrowserCheckbox = new Button(panel, SWT.CHECK);
        useExternalBrowserCheckbox.setText("Use an external Web browser");
        useExternalBrowserCheckbox.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
        if (!browserSupport.isInternalWebBrowserAvailable()) {
            useExternalBrowserCheckbox.setSelection(true);
            useExternalBrowserCheckbox.setEnabled(false);
        }
        
        // Prepare extra description.
        Label extraDescription = new Label(panel, SWT.LEFT);
        extraDescription.setText("NOTE: Some social-related functions might not work here\n"
        		+ "unless you check some options via the OSDE feature:\n"
        		+ "iGoogle - Add Gadget.");
        extraDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));


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
        password = passwordText.getText();
        hostProjectName = hostProjectNameText.getText();
        useCanvasView = canvasViewButton.getSelection();
        logger.fine("useCanvasView: " + useCanvasView);
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

    String getHostProjectName() {
    	return hostProjectName;
    }

    boolean isUseCanvasView() {
        return useCanvasView;
    }

    boolean isUseExternalBrowser() {
        return useExternalBrowser;
    }
}
