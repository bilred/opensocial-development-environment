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
import com.googlecode.osde.internal.utils.Logger;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public abstract class AbstractBinder<T> {

    protected static Logger logger = new Logger(AbstractBinder.class);
    protected String preferenceName;
    protected T control;

    /**
     * To create a binding relation between the SWT control and Preference name 
     * @param control
     * @param preferenceName
     */
    public AbstractBinder(T control, String preferenceName) {
        this.control = control;
        this.preferenceName = preferenceName;
    }

    /**
     * load the value from OsdePreferencesModel and assign to SWT control
     * @param model
     * @throws Exception
     */
    public abstract void doLoad(OsdePreferencesModel model) throws Exception;

    /**
     * load the default value from OsdePreferencesModel and assign to SWT control
     * @param model
     * @throws Exception
     */
    public abstract void doLoadDefault(OsdePreferencesModel model) throws Exception;

    /**
     * get data from SWT control and store to OsdePreferencesModel
     * @param model
     * @throws Exception
     */
    public abstract void doSave(OsdePreferencesModel model) throws Exception;
    
    @Override
    public String toString() {
        return "binder[" + preferenceName + "]::" + control;
    }
}