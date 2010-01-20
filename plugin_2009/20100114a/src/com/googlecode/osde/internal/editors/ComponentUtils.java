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
package com.googlecode.osde.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Utility class for SWT components.
 */
public class ComponentUtils {

    public static Label createLabel(Composite parent, FormToolkit toolkit, String text) {
        Label label = toolkit.createLabel(parent, text);
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = SWT.BEGINNING;
        layoutData.verticalIndent = 4;
        label.setLayoutData(layoutData);
        return label;
    }

    public static Text createText(Composite parent, FormToolkit toolkit, Listener modifyListener) {
        return createText(parent, toolkit, 1, modifyListener);
    }

    public static Text createText(Composite parent, FormToolkit toolkit, int span,
            Listener modifyListener) {
        Text text = toolkit.createText(parent, "", SWT.BORDER);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = span;
        text.setLayoutData(layoutData);
        text.addListener(SWT.Modify, modifyListener);
        return text;
    }

    public static Text createTextArea(Composite parent, FormToolkit toolkit, int span,
            Listener modifyListener) {
        Text text = toolkit.createText(parent, "", SWT.BORDER | SWT.MULTI);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = span;
        layoutData.heightHint = 30;
        text.setLayoutData(layoutData);
        text.addListener(SWT.Modify, modifyListener);
        return text;
    }

    public static Button createRadio(Composite parent, FormToolkit toolkit, String text,
            Listener modifyListener) {
        return createRadio(parent, toolkit, text, 1, modifyListener, GridData.FILL_HORIZONTAL);
    }

    public static Button createRadio(Composite parent, FormToolkit toolkit, String text, int span,
            Listener modifyListener) {
        return createRadio(parent, toolkit, text, span, modifyListener, GridData.FILL_HORIZONTAL);
    }

    public static Button createRadio(Composite parent, FormToolkit toolkit, String text, int span,
            Listener modifyListener, int gridData) {
        Button button = toolkit.createButton(parent, text, SWT.RADIO);
        GridData layoutData;
        if (gridData != 0) {
            layoutData = new GridData(gridData);
        } else {
            layoutData = new GridData();
        }
        layoutData.horizontalSpan = span;
        button.setLayoutData(layoutData);
        button.addListener(SWT.Selection, modifyListener);
        return button;
    }

}
