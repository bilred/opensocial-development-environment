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
package com.googlecode.osde.internal.runtime;

import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.ConnectionException;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.shindig.PersonService;
import com.googlecode.osde.internal.ui.wizards.newrestprj.NewRestfulAccessProjectResourceWizard;
import com.googlecode.osde.internal.ui.wizards.newrestprj.SimpleConfigurationElementImpl;
import com.googlecode.osde.internal.utils.ApplicationInformation;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import com.google.gadgets.parser.ParserException;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action for creating a Java Project.
 */
public class CreateJavaProjectAction implements IObjectActionDelegate {

    private IFile file;
    private Shell shell;
    private IStructuredSelection currentSelection;
    private IWorkbenchPart targetPart;

    public CreateJavaProjectAction() {
        super();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
        this.targetPart = targetPart;
    }

    public void run(IAction action) {
        try {
            ApplicationInformation appInfo = OpenSocialUtil.createApplicationInformation(file);
            ApplicationService service = Activator.getDefault().getApplicationService();
            ApplicationImpl application = service.getApplication(appInfo.getAppId());
            Person person = findPersonWithFriends();
            if (person != null) {
                if (application != null) {
                    NewRestfulAccessProjectResourceWizard wizard =
                            new NewRestfulAccessProjectResourceWizard();
                    wizard.setApplication(application);
                    wizard.setPerson(person);
                    wizard.init(targetPart.getSite().getWorkbenchWindow().getWorkbench(),
                            currentSelection);
                    wizard.setInitializationData(new SimpleConfigurationElementImpl() {
                        @Override
                        public String getAttribute(String name)
                                throws InvalidRegistryObjectException {
                            if (name.equals("finalPerspective") || name
                                    .equals("preferredPerspectives")) {
                                return JavaUI.ID_PERSPECTIVE;
                            } else {
                                return null;
                            }
                        }
                    }, null, null);
                    WizardDialog dialog = new WizardDialog(shell, wizard);
                    dialog.open();
                } else {
                    MessageDialog
                            .openWarning(shell, "Warning", "This application does not run yet.");
                }
            } else {
                MessageDialog.openError(shell, "Error", "There is no person in Shindig database.");
            }
        } catch (CoreException e) {
            MessageDialog.openError(shell, "Error", "Invalid gadget file. " + e.getMessage());
        } catch (ConnectionException e) {
            MessageDialog.openError(shell, "Error", "Shindig database not started yet.");
        } catch (ParserException e) {
            MessageDialog.openError(shell, "Error", "Invalid syntax. " + e.getMessage());
        }
    }

    private Person findPersonWithFriends() throws ConnectionException {
        PersonService service = Activator.getDefault().getPersonService();
        List<Person> people = service.getPeople();
        if (people.isEmpty()) {
            return null;
        } else {
            for (Person person : people) {
                List<RelationshipImpl> relationshipList = service.getRelationshipList(person);
                for (RelationshipImpl relation : relationshipList) {
                    if ("friends".equals(relation.getGroupId())) {
                        return person;
                    }
                }
            }
            return people.get(0);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        file = null;
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            currentSelection = structured;
            Object element = structured.getFirstElement();
            if (element instanceof IFile) {
                file = (IFile) element;
            }
        }
    }

}
