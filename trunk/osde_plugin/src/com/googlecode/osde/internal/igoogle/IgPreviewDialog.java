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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

    private boolean usePreviousIgCredentials;
    private Button usePreviousIgCredentialsButton;
    private String username;
    private Text usernameText;
    private String password;
    private Text passwordText;
    private String hostProjectName;
    private Text hostProjectNameText;
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

        // Disable help dialog.
        setDialogHelpAvailable(false);

        // Set title and message.
        setTitle("Preview Gadget on iGoogle");
        setMessage("The allows you to preview a gadget on iGoogle.");

        // Prepare dialog area composite.
        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);

        // Prepare credentialsComposite.
        prepareCredentialsComposite(dialogAreaComposite);

        // Prepare panel composite.
        Composite panelComposite = new Composite(dialogAreaComposite, SWT.NONE);
        panelComposite.setLayout(new GridLayout(3, true));
        panelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare host-project-name.
        Label hostProjectNameLabel = new Label(panelComposite, SWT.LEFT);
        hostProjectNameLabel.setText("Project name: ");
        hostProjectNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        hostProjectNameText = new Text(panelComposite, SWT.SINGLE);
        hostProjectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        hostProjectNameText.setText(IgPreviewJob.HOST_PROJECT_NAME_FOR_PREVIEW);

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

    private void prepareCredentialsComposite(Composite dialogAreaComposite) {
        // Prepare credentialsComposite.
        Composite credentialsComposite = new Composite(dialogAreaComposite, SWT.NONE);
        credentialsComposite.setLayout(new GridLayout(3, true));
        credentialsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare useCurrentIgCredentialsButton.
        usePreviousIgCredentialsButton = new Button(credentialsComposite, SWT.RADIO);
        usePreviousIgCredentialsButton.setText("Use the previous Google account session");
        usePreviousIgCredentialsButton.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        // Prepare useNewIgCredentialsButton.
        Button useNewIgCredentialsButton = new Button(credentialsComposite, SWT.RADIO);
        useNewIgCredentialsButton.setText("Use the following Google account");
        useNewIgCredentialsButton.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        // Prepare newCredentialsComposite which is used for username and password.
        Composite newCredentialsComposite = new Composite(dialogAreaComposite, SWT.NONE);
        newCredentialsComposite.setLayout(new GridLayout(3, true));
        newCredentialsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare username.
        Label usernameLabel = new Label(newCredentialsComposite, SWT.LEFT);
        usernameLabel.setText("\tUsername: ");
        usernameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        usernameText = new Text(newCredentialsComposite, SWT.SINGLE);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Set mouse focus.
        // If this text field is unavailable, the focus will be passed to the next available one.
        usernameText.setFocus();

        // Prepare password.
        Label passwordLabel = new Label(newCredentialsComposite, SWT.LEFT);
        passwordLabel.setText("\tPassword: ");
        passwordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        passwordText = new Text(newCredentialsComposite, SWT.SINGLE);
        passwordText.setEchoChar('*');
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare selection listeners for radio buttons.
        usePreviousIgCredentialsButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing.
            }

            public void widgetSelected(SelectionEvent e) {
                usernameText.setEnabled(false);
                passwordText.setEnabled(false);
            }
        });
        useNewIgCredentialsButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing.
            }

            public void widgetSelected(SelectionEvent e) {
                usernameText.setEnabled(true);
                passwordText.setEnabled(true);
            }
        });

        // Configure "selection" and "enabled" initially.
        boolean hasCurrentIgCredentials = IgCredentials.hasCurrentInstance();
        if (hasCurrentIgCredentials) {
            usePreviousIgCredentialsButton.setEnabled(true);
            usePreviousIgCredentialsButton.setSelection(true);
            usernameText.setEnabled(false);
            passwordText.setEnabled(false);
        } else {
            usePreviousIgCredentialsButton.setEnabled(false);
            useNewIgCredentialsButton.setSelection(true);
        }
    }

    @Override
    protected Point getInitialSize() {
        logger.fine("getInitialSize");
        return new Point(350, 350);
    }

    @Override
    protected void okPressed() {
        logger.fine("okPressed");

        usePreviousIgCredentials = usePreviousIgCredentialsButton.getSelection();
        username = usernameText.getText();
        password = passwordText.getText();
        hostProjectName = hostProjectNameText.getText();
        useCanvasView = useCanvasViewButton.getSelection();
        useExternalBrowser = useExternalBrowserCheckbox.getSelection();

        logger.fine("gonna setReturnCode");
        setReturnCode(OK);
        logger.fine("gonna close");
        close();
    }

    boolean isUsePreviousIgCredentials() {
        return usePreviousIgCredentials;
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
