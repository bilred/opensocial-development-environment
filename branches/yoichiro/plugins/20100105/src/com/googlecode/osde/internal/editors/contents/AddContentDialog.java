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
package com.googlecode.osde.internal.editors.contents;

import com.google.gadgets.ViewType;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddContentDialog extends TitleAreaDialog {

    private Combo typeCombo;
    private Text viewText;

    private ViewType type;
    private String view;

    public AddContentDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("Add the supported content");
        setMessage("Please input the information about content.");
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        panel.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        panel.setLayoutData(layoutData);
        //
        Label label = new Label(panel, SWT.NONE);
        label.setText("Type:");
        typeCombo = new Combo(panel, SWT.READ_ONLY);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        typeCombo.setLayoutData(layoutData);
        for (int i = 0; i < ViewType.values().length; i++) {
            typeCombo.add(ViewType.values()[i].name());
        }
        typeCombo.select(0);
        //
        label = new Label(panel, SWT.NONE);
        label.setText("View:");
        viewText = new Text(panel, SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        viewText.setLayoutData(layoutData);
        viewText.setToolTipText("For example: 'canvas,profile', 'preview'.");
        //
        return composite;
    }

    @Override
    protected void okPressed() {
        view = viewText.getText();
        type = ViewType.parse(typeCombo.getText());
        setReturnCode(OK);
        close();
    }

    public String getView() {
        return view;
    }

    public ViewType getType() {
        return type;
    }

}
