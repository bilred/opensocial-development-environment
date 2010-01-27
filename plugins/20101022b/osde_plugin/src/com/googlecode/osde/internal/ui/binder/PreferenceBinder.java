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
package com.googlecode.osde.internal.ui.binder;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.osde.internal.OsdePreferencesModel;
import com.googlecode.osde.internal.utils.Logger;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class PreferenceBinder {

    private static Logger logger = new Logger(PreferenceBinder.class);

    Set<AbstractBinder<?>> binders = new HashSet<AbstractBinder<?>>();

    public void addBinder(AbstractBinder<?> binder) {
        this.binders.add(binder);
    }

    public void doLoad(OsdePreferencesModel model) {
        for (AbstractBinder<?> binder : binders) {
            try {
                binder.doLoad(model);
            } catch (Exception e) {
                logger.error("Failed to " + binder + ".doLoad()", e);
            }
        }
    }

    public void doLoadDefault(OsdePreferencesModel model) {
        for (AbstractBinder<?> binder : binders) {
            try {
                binder.doLoadDefault(model);
            } catch (Exception e) {
                logger.error("Failed to " + binder + ".doLoadDefault()", e);
            }
        }
    }

    public void doSave(OsdePreferencesModel model) {
        for (AbstractBinder<?> binder : binders) {
            try {
                binder.doSave(model);
            } catch (Exception e) {
                logger.error("Failed to " + binder + ".doSave()", e);
            }
        }
    }

}
