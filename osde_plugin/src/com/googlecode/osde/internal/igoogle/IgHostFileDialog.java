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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The popup dialog asking for users authentication info in order
 * for hosting gadget files to iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgHostFileDialog extends IgCredentialsDialog {
    private static Logger logger = new Logger(IgHostFileDialog.class);

    public IgHostFileDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.fine("createDialogArea");

        // Set title and message.
        setTitle("Host gadget files on iGoogle");
        setMessage("This allows you to host your gadget files on iGoogle.");

        // Disable help button.
        setHelpAvailable(false);

        // Prepare dialog area composite.
        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
        prepareComposite(dialogAreaComposite);

        // Prepare panel composite.
        Composite panelComposite = new Composite(dialogAreaComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, true);
        panelComposite.setLayout(layout);
        panelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare extra description.
        Label extraDescription = new Label(panelComposite, SWT.LEFT);
        extraDescription.setText(
        		"\nNote: It may take up to 24 hours for your latest changes to\ntake effect.");
        extraDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        return dialogAreaComposite;
    }
}
