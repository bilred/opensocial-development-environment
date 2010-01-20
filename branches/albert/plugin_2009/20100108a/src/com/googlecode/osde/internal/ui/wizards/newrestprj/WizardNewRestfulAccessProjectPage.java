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
package com.googlecode.osde.internal.ui.wizards.newrestprj;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewRestfulAccessProjectPage extends WizardPage {

    private Text sourceDirectoryNameText;
    private Text binaryDirectoryNameText;
    private Text libraryDirectoryNameText;

    private ModifyListener listener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
            validatePage();
        }
    };

    protected WizardNewRestfulAccessProjectPage(String pageName) {
        super(pageName);
        setPageComplete(false);
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        //
        Label label = new Label(composite, SWT.NONE);
        label.setText("Source folder name:");
        sourceDirectoryNameText = new Text(composite, SWT.BORDER);
        sourceDirectoryNameText.setText("src");
        sourceDirectoryNameText.addModifyListener(listener);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        sourceDirectoryNameText.setLayoutData(layoutData);
        //
        label = new Label(composite, SWT.NONE);
        label.setText("Output folder name:");
        binaryDirectoryNameText = new Text(composite, SWT.BORDER);
        binaryDirectoryNameText.setText("bin");
        binaryDirectoryNameText.addModifyListener(listener);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        binaryDirectoryNameText.setLayoutData(layoutData);
        //
        label = new Label(composite, SWT.NONE);
        label.setText("Library folder name:");
        libraryDirectoryNameText = new Text(composite, SWT.BORDER);
        libraryDirectoryNameText.setText("lib");
        libraryDirectoryNameText.addModifyListener(listener);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        libraryDirectoryNameText.setLayoutData(layoutData);
        //
        setPageComplete(validatePage());
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
        Dialog.applyDialogFont(composite);
    }

    private boolean validatePage() {
        String sourceDirectoryName = sourceDirectoryNameText.getText().trim();
        if (sourceDirectoryName.length() == 0) {
            setErrorMessage(null);
            setMessage("Source folder name is empty.");
            return false;
        }
        String binaryDirectoryName = binaryDirectoryNameText.getText().trim();
        if (binaryDirectoryName.length() == 0) {
            setErrorMessage(null);
            setMessage("Output folder name is empty.");
            return false;
        }
        String libraryDirectoryName = libraryDirectoryNameText.getText().trim();
        if (libraryDirectoryName.length() == 0) {
            setErrorMessage(null);
            setMessage("Library folder name is empty.");
            return false;
        }
        setErrorMessage(null);
        setMessage(null);
        return true;
    }

    public String getSourceDirectoryName() {
        return sourceDirectoryNameText.getText();
    }

    public String getBinaryDirectoryName() {
        return binaryDirectoryNameText.getText();
    }

    public String getLibraryDirectoryName() {
        return libraryDirectoryNameText.getText();
    }

}
