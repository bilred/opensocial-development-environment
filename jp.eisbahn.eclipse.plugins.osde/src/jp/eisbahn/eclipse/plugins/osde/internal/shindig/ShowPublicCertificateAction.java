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
 * specific language governing permissions and limitations under the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ShowPublicCertificateAction extends Action implements IWorkbenchWindowActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;
	
	private static final String PUBLIC_CER =
		  "-----BEGIN CERTIFICATE-----\n"
		+ "MIICDDCCAXWgAwIBAgIJAJ4W4bqTjEGSMA0GCSqGSIb3DQEBBQUAMA8xDTALBgNV\n"
		+ "BAMTBG9zZGUwHhcNMDkwMTA3MDg1MTEwWhcNMTAwMTA3MDg1MTEwWjAPMQ0wCwYD\n"
		+ "VQQDEwRvc2RlMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDAv7N2y4S1TWgL\n"
		+ "1UlaqivxGKyskl5o5TLoZdli2AMHidjVzTJsGX1ydYWJmteicnVLTo//JrmkB19C\n"
		+ "L+IYCOnxo6H5YdNAi+t7e3NRpMJF6YBCV+MXnP2dZx02SHAhO6z4i8P/EMcnNX6l\n"
		+ "sX7cXeMnmYS+X7AG+AS6zVVdSKQwVwIDAQABo3AwbjAdBgNVHQ4EFgQUbONO5jjA\n"
		+ "q8gC34J+iUfAnkZVelswPwYDVR0jBDgwNoAUbONO5jjAq8gC34J+iUfAnkZVeluh\n"
		+ "E6QRMA8xDTALBgNVBAMTBG9zZGWCCQCeFuG6k4xBkjAMBgNVHRMEBTADAQH/MA0G\n"
		+ "CSqGSIb3DQEBBQUAA4GBABzjqyU0s8Kn73dmiK1wh2OgoSGVcHKr8ELHg7pf4J9S\n"
		+ "fkg+OFTYajJhS1lDi3uyMTM486oxi1QxUY7f/c/iDnq09eV914ZEhMhalhD+H9WC\n"
		+ "WThZt1a1SCmWx1Ne2a5O6qeQPtFPSL/BEe6xFWE+0RHYWEK+JcDRhv5MDW0DrJjT\n"
		+ "-----END CERTIFICATE-----";
	
	public ShowPublicCertificateAction() {
		super();
	}
	
	public void run(IAction action) {
		ImageDescriptor imageDescriptor =
			Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_key.gif");
		MessageDialog dialog = new MessageDialog(
				shell, "Public Certificate", imageDescriptor.createImage(),
				"This is Public Certificate for OAuth.", MessageDialog.INFORMATION,
				new String[] {"OK"}, 0) {
					@Override
					protected Control createCustomArea(Composite parent) {
						Composite composite = new Composite(parent, SWT.NONE);
						composite.setLayout(new GridLayout());
						GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
						composite.setLayoutData(layoutData);
						Text text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
						text.setText(PUBLIC_CER);
						text.setSelection(0, PUBLIC_CER.length());
						text.setEditable(false);
						layoutData = new GridData(GridData.FILL_HORIZONTAL);
						layoutData.heightHint = 250;
						text.setLayoutData(layoutData);
						return parent;
					}
		};
		dialog.open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		targetPart = window.getActivePage().getActivePart();
		shell = targetPart.getSite().getShell();
	}
	
}
