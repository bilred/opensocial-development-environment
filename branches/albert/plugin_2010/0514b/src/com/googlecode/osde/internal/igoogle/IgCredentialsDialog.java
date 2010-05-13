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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog asking for users authentication info and hosting project name.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgCredentialsDialog extends TitleAreaDialog {
    private static Logger logger = new Logger(IgCredentialsDialog.class);

    private boolean usePreviousIgCredentials;
    private Button usePreviousIgCredentialsButton;
    private String username;
    private Text usernameText;
    private String password;
    private Text passwordText;
    private String hostProjectName;
    private Text hostProjectNameText;

    public IgCredentialsDialog(Shell shell) {
        super(shell);
    }

    void prepareComposite(Composite dialogAreaComposite) {
    	logger.fine("in prepareComposite");

        // Prepare credentialsComposite.
        Composite credentialsComposite = new Composite(dialogAreaComposite, SWT.NONE);
        credentialsComposite.setLayout(new GridLayout(3, true));
        credentialsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare host-project-name.
        Label hostProjectNameLabel = new Label(credentialsComposite, SWT.LEFT);
        hostProjectNameLabel.setText("Project name: ");
        hostProjectNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        hostProjectNameText = new Text(credentialsComposite, SWT.SINGLE);
        hostProjectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        hostProjectNameText.setText(IgPreviewJob.HOST_PROJECT_NAME_FOR_PREVIEW);
        hostProjectNameText.setFocus();

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

        // Prepare usernamePasswordComposite.
        Composite usernamePasswordComposite = new Composite(dialogAreaComposite, SWT.NONE);
        usernamePasswordComposite.setLayout(new GridLayout(3, true));
        usernamePasswordComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare username.
        Label usernameLabel = new Label(usernamePasswordComposite, SWT.LEFT);
        usernameLabel.setText("\tUsername: ");
        usernameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        usernameText = new Text(usernamePasswordComposite, SWT.SINGLE);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        // Prepare password.
        Label passwordLabel = new Label(usernamePasswordComposite, SWT.LEFT);
        passwordLabel.setText("\tPassword: ");
        passwordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        passwordText = new Text(usernamePasswordComposite, SWT.SINGLE);
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
                hostProjectNameText.setFocus();
            }
        });
        useNewIgCredentialsButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing.
            }

            public void widgetSelected(SelectionEvent e) {
                usernameText.setEnabled(true);
                passwordText.setEnabled(true);
                usernameText.setFocus();
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
        return new Point(350, 300);
    }

    @Override
    protected void okPressed() {
        usePreviousIgCredentials = usePreviousIgCredentialsButton.getSelection();
        username = usernameText.getText();
        password = passwordText.getText();
        hostProjectName = hostProjectNameText.getText();
        setReturnCode(OK);
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
}
