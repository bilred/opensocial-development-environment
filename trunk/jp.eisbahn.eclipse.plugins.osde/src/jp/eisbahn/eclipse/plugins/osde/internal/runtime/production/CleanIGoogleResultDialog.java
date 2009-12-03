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

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The result dialog confirming that the hosted files are cleaned.
 *
 * @author albert.cheng.ig@gmail.com
 */
// TODO: (p1) Use a Dialog with only an OK button.
//       TitleAreaDialog has an unnecessary CANCEL button.
//       Try MessageDialog.
public class CleanIGoogleResultDialog extends TitleAreaDialog {
    private static Logger logger = new Logger(CleanIGoogleResultDialog.class);

    public CleanIGoogleResultDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.info("createDialogArea");

        // Set title and message.
        setTitle("iGoogle - Clean Preview Files");
        setMessage("Successful.");

        // Prepare composite and panel.
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        panel.setLayout(new GridLayout(1, true));
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare label.
        Label label = new Label(panel, SWT.LEFT);
        label.setText("You gadget files as hosted on iGoogle\nfor preview are all deleted.");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        return composite;
    }

    @Override
    protected Point getInitialSize() {
        logger.info("getInitialSize");
        return new Point(400, 300);
    }

    @Override
    protected void okPressed() {
        logger.info("okPressed");
        setReturnCode(OK);
        close();
    }
}
