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

import org.apache.commons.lang.StringUtils;

import org.eclipse.swt.widgets.Combo;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class ComboBinder extends AbstractBinder<Combo> {

    public ComboBinder(Combo control, String preferenceName) {
        super(control, preferenceName);
    }

    @Override
    public void doLoad(OsdePreferencesModel model) throws Exception {
        select(model.get(preferenceName));
    }

    @Override
    public void doLoadDefault(OsdePreferencesModel model) throws Exception {
        select(model.getDefault(preferenceName));
    }

    protected boolean compare(String storedValue, String comboItem) {
        return StringUtils.equals(storedValue, comboItem);
    }

    /**
     * Select a item in the combo control. The item value is equals to store data 
     * @param value
     */
    protected void select(String value) {
        for (int index = 0; index < control.getItemCount(); index++) {
            if (compare(value, control.getItem(index))) {
                try {
                    control.select(index);
                } catch (Exception e) {
                    logger.error("select item failure @index = " + index, e);
                }

                break;
            }
        }
    }

    @Override
    public void doSave(OsdePreferencesModel model) throws Exception {
        model.store(preferenceName, control.getText());
    }

}
