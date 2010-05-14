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

package com.googlecode.osde.internal;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.osde.internal.OsdeConfig.PreferenceGetter;
import com.googlecode.osde.internal.common.JdkVersion;
import com.googlecode.osde.internal.utils.Logger;
import com.googlecode.osde.internal.utils.MapUtil;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.Preferences;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 *
 */
public class OsdePreferencesModel {

    private static final Logger logger = new Logger(OsdePreferencesModel.class);
    private IPreferenceStore store;

    public OsdePreferencesModel(IPreferenceStore store) {
        this.store = store;
    }

    public static void initializeDefaultPreferences() {
        String tmpdir = Activator.getDefault().getWorkDirectory().getAbsolutePath();
        Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
        Locale locale = Locale.getDefault();
        node.put(OsdeConfig.DEFAULT_COUNTRY, locale.getCountry());
        node.put(OsdeConfig.DEFAULT_LANGUAGE, locale.getLanguage());
        File file = new File(tmpdir, "osde_db");
        file.mkdirs();
        node.put(OsdeConfig.DATABASE_DIR, file.getAbsolutePath());
        node.put(OsdeConfig.DOCS_SITE_MAP, "");
        file = new File(tmpdir, "osde_jetty");
        file.mkdirs();
        node.put(OsdeConfig.JETTY_DIR, file.getAbsolutePath());
        node.put(OsdeConfig.USE_INTERNAL_DATABASE, "true");
        node.put(OsdeConfig.EXTERNAL_DATABASE_TYPE, "MySQL");
        node.put(OsdeConfig.EXTERNAL_DATABASE_HOST, "localhost");
        node.put(OsdeConfig.EXTERNAL_DATABASE_PORT, "3306");
        node.put(OsdeConfig.EXTERNAL_DATABASE_USERNAME, "");
        node.put(OsdeConfig.EXTERNAL_DATABASE_PASSWORD, "");
        node.put(OsdeConfig.EXTERNAL_DATABASE_NAME, "osde");
        String userHome = System.getProperty("user.home");
        file = new File(userHome, Activator.WORK_DIR_NAME);
        file.mkdirs();
        node.put(OsdeConfig.WORK_DIRECTORY, file.getAbsolutePath());
        node.put(OsdeConfig.LOGGER_CONFIG_FILE, "");
        node.putBoolean(OsdeConfig.COMPILE_JAVASCRIPT, JdkVersion.isAtLeastJdk6());
        node.put(OsdeConfig.FIREFOX_LOCATION, OsdeConfig.DEFAULT_FIREFOX_LOCATION);
    }

    public OsdeConfig getOsdeConfiguration() {

        PreferenceGetter commonPreferenceGetter = new PreferenceGetter() {
            public boolean getBoolean(OsdePreferencesModel model, String name) {
                return model.getBoolean(name);
            }
            public String get(OsdePreferencesModel model, String name) {
                return model.get(name);
            }
        };

        return new OsdeConfig(this, commonPreferenceGetter);
    }

    public OsdeConfig getDefaultOsdeConfiguration() {
        PreferenceGetter defaultPrefereceGetter = new PreferenceGetter() {
            public boolean getBoolean(OsdePreferencesModel model, String name) {
                return model.getDefaultBoolean(name);
            }
            public String get(OsdePreferencesModel model, String name) {
                return model.getDefault(name);
            }
        };

        return new OsdeConfig(this, defaultPrefereceGetter);
    }

    public void store(Map<String, Object> values) {
        for (Entry<String, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                store(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof Boolean) {
                store(entry.getKey(), (Boolean) entry.getValue());
            } else {
                logger.warn("can not store preference name["
                        + entry.getKey() + "] = " + entry.getValue());
            }
        }
    }

    public void store(String name, Map<String, String> map){
        try {
            store(name, MapUtil.toString(map));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void store(String name, String value){
        store.setValue(name, value);
    }

    public void store(String name, boolean value){
        store.setValue(name, value);
    }

    public String get(String name) {
        return store.getString(name);
    }

    public boolean getBoolean(String name) {
        return store.getBoolean(name);
    }

    public String getDefault(String name){
        return store.getDefaultString(name);
    }

    public boolean getDefaultBoolean(String name){
        return store.getDefaultBoolean(name);
    }

}