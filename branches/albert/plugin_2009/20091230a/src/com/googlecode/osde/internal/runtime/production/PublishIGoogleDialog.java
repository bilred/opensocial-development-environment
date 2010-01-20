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

import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The popup dialog asking for users authentication info etc in order
 * for publishing gadget on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class PublishIGoogleDialog extends TitleAreaDialog {
    private static Logger logger = new Logger(PublishIGoogleDialog.class);

    private String username;
    private String password;
    private String projectName;
    private Text usernameText;
    private Text passwordText;
    private Text projectNameText;

    public PublishIGoogleDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.fine("createDialogArea");

        // Set title and message.
        setTitle("Publish Gadget on iGoogle");
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

        // Prepare project name.
        Label projectNameLabel = new Label(panel, SWT.LEFT);
        projectNameLabel.setText("Project name: ");
        projectNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        projectNameText = new Text(panel, SWT.SINGLE);
        projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

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
        projectName = projectNameText.getText();
        logger.fine("projectName: " + projectName);

        setReturnCode(OK);
        close();
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getProjectName() {
        return projectName;
    }
}
