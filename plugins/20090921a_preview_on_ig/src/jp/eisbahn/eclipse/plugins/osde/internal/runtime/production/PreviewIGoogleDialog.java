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

import java.util.logging.Logger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * The popup dialog asking for users authentication info in order
 * for previewing gadget on iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleDialog extends TitleAreaDialog {
    Logger logger = Logger.getLogger(PreviewIGoogleDialog.class.getName());

    private String username;
    private String password;
	private boolean useExternalBrowser;
	private Button useExternalBrowserCheck;

	public PreviewIGoogleDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
	    logger.info("createDialogArea");

	    // Set title and message.
		setTitle("Preview Gadget on iGoogle");
		setMessage("Please enter your gmail account info.");

		// Prepare composite and panel.
		Composite composite = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		panel.setLayoutData(layoutData);

	    // TODO: prepare username and password

		// Prepare checkbox of useExternalBrowser.
		useExternalBrowserCheck = new Button(panel, SWT.CHECK);
		useExternalBrowserCheck.setText("Use an external Web browser.");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		useExternalBrowserCheck.setLayoutData(layoutData);
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		if (!browserSupport.isInternalWebBrowserAvailable()) {
			useExternalBrowserCheck.setSelection(true);
			useExternalBrowserCheck.setEnabled(false);
		}

		return composite;
	}

	@Override
	protected Point getInitialSize() {
        logger.info("getInitialSize");
		return new Point(550, 400);
	}

	@Override
	protected void okPressed() {
        logger.info("okPressed");

		useExternalBrowser = useExternalBrowserCheck.getSelection();
		logger.info("useExternalBrowser: " + useExternalBrowser);

		logger.info("gonna setReturnCode");
		setReturnCode(OK);
		logger.info("gonna close");
		close();
	}

	String getUserName() {
	    return username;
	}

	String getPassword() {
	    return password;
	}

	boolean isUseExternalBrowser() {
		return useExternalBrowser;
	}
}
