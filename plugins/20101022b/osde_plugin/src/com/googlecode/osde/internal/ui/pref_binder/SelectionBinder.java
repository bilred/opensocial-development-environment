/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.ui.pref_binder;

import com.googlecode.osde.internal.OsdePreferencesModel;

import org.eclipse.swt.widgets.Button;


/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class SelectionBinder extends AbstractBinder<Button>{

    public SelectionBinder(Button control, String preferenceName) {
        super(control, preferenceName);
    }

    @Override
    public void doLoad(OsdePreferencesModel model) throws Exception {
        control.setSelection(model.getBoolean(preferenceName));
    }

    @Override
    public void doLoadDefault(OsdePreferencesModel model) throws Exception {
        control.setSelection(model.getDefaultBoolean(preferenceName));
    }

    @Override
    public void doSave(OsdePreferencesModel model) throws Exception {
        model.store(preferenceName, control.getSelection());
    }

}
