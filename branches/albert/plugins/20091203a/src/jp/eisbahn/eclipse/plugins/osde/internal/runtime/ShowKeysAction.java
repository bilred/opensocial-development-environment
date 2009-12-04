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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.OpenSocialUtil;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.gadgets.parser.ParserException;

/**
 * Action to show the Consumer key and the secret for accessing APIs
 * with RESTful Protocol.
 */
public class ShowKeysAction implements IObjectActionDelegate {

	private IFile file;
	private Shell shell;

	public ShowKeysAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {
		try {
			ApplicationInformation appInfo = OpenSocialUtil.createApplicationInformation(file);
			ApplicationService service = Activator.getDefault().getApplicationService();
			final ApplicationImpl application = service.getApplication(appInfo.getAppId());
			if (application != null) {
				ImageDescriptor imageDescriptor =
					Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_key.gif");
				MessageDialog dialog = new MessageDialog(
						shell, "Application information", imageDescriptor.createImage(),
						"This is keys for accessing from external service.", MessageDialog.INFORMATION,
						new String[] {"OK"}, 0) {
							@Override
							protected Control createCustomArea(Composite parent) {
								Composite composite = new Composite(parent, SWT.NONE);
								GridLayout gridLayout = new GridLayout();
								gridLayout.numColumns = 2;
								composite.setLayout(gridLayout);
								GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
								composite.setLayoutData(layoutData);
								Label label = new Label(composite, SWT.NONE);
								label.setText("Title:");
								label = new Label(composite, SWT.NONE);
								label.setText(application.getTitle());
								label = new Label(composite, SWT.NONE);
								label.setText("Path:");
								label = new Label(composite, SWT.NONE);
								label.setText(application.getPath());
								label = new Label(composite, SWT.NONE);
								label.setText("Consumer Key:");
								Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
								text.setText(application.getConsumerKey());
								text.setEditable(false);
								text.setSelection(0);
								layoutData = new GridData(GridData.FILL_HORIZONTAL);
								layoutData.heightHint = 20;
								text.setLayoutData(layoutData);
								label = new Label(composite, SWT.NONE);
								label.setText("Consumer Secret:");
								text = new Text(composite, SWT.BORDER | SWT.MULTI);
								text.setText(application.getConsumerSecret());
								text.setEditable(false);
								text.setSelection(0);
								layoutData = new GridData(GridData.FILL_HORIZONTAL);
								layoutData.heightHint = 20;
								text.setLayoutData(layoutData);
								return parent;
							}
				};
				dialog.open();
			} else {
				MessageDialog.openWarning(shell, "Warning", "This application does not run yet.");
			}
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		} catch (ConnectionException e) {
			MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
		} catch (ParserException e) {
			MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		file = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection)selection;
			Object element = structured.getFirstElement();
			if (element instanceof IFile) {
				file = (IFile)element;
			}
		}
	}

}
