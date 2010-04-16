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
 * for adding a gadget to iGoogle page.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgAddItDialog extends TitleAreaDialog {
    private static Logger logger = new Logger(IgAddItDialog.class);
    
    /**
     * The static variable stores current gadgetUrl.
     * It is for the convenience purpose to help users to enter gadgetUrl.
     */
    private static String currentGadgetUrl = "";
    static void setCurrentGadgetUrl(String newValue) {
    	currentGadgetUrl = newValue;
    }
    static String getCurrentGadgetUrl() {
    	return currentGadgetUrl;
    }

    private String gadgetUrl;
    private boolean useExternalBrowser;
    private Text gadgetUrlText;
    private Button useExternalBrowserCheckbox;

    public IgAddItDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        logger.fine("createDialogArea");
        
        // Disable help dialog.
        setDialogHelpAvailable(false);

        // Set title and message.
        setTitle("Add a gadget to your iGoogle page");
        setMessage("This allows you to add this gadget to your iGoogle page.");

        // Prepare composite and panel.
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(3, true);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Prepare gadgetUrl.
        Label gadgetUrlLabel = new Label(panel, SWT.LEFT);
        gadgetUrlLabel.setText("Gadget URL: ");
        gadgetUrlLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        gadgetUrlText = new Text(panel, SWT.SINGLE);
        gadgetUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        gadgetUrlText.setText(currentGadgetUrl);
        gadgetUrlText.setFocus();

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
        extraDescription.setText(
        		"\nNOTE: Please remember to login to your iGoogle page on browser.\n");
        extraDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        return composite;
    }

    @Override
    protected Point getInitialSize() {
        logger.fine("getInitialSize");
        return new Point(750, 300);
    }

    @Override
    protected void okPressed() {
        logger.fine("okPressed");

        gadgetUrl = gadgetUrlText.getText();
        useExternalBrowser = useExternalBrowserCheckbox.getSelection();

        setReturnCode(OK);
        close();
    }

    String getGadgetUrl() {
    	return gadgetUrl;
    }

    boolean isUseExternalBrowser() {
        return useExternalBrowser;
    }
}
