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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.Preferences;

import com.googlecode.osde.internal.common.JdkVersion;
import com.googlecode.osde.internal.utils.Logger;

/**
 * 
 * @author chingyichan.tw@gmail.com (qrtt1)
 *
 */
public class OsdePreferencesModel {

    private static final Logger logger = new Logger(OsdePreferencesModel.class);
    private IPreferenceStore store;

    public OsdePreferencesModel(IPreferenceStore store) {
        this.store = store;
    }

    public void initializeDefaultPreferences() {
        String tmpdir = Activator.getDefault().getWorkDirectory()
                .getAbsolutePath();
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
        try {
            OsdeConfig config = new OsdeConfig();
            config.setDefaultCountry(store.getString(OsdeConfig.DEFAULT_COUNTRY));
            config.setDefaultLanguage(store.getString(OsdeConfig.DEFAULT_LANGUAGE));
            config.setDatabaseDir(store.getString(OsdeConfig.DATABASE_DIR));
            config.setDocsSiteMap(decodeSiteMap(store.getString(OsdeConfig.DOCS_SITE_MAP)));
            config.setJettyDir(store.getString(OsdeConfig.JETTY_DIR));
            config.setUseInternalDatabase(store.getBoolean(OsdeConfig.USE_INTERNAL_DATABASE));
            config.setExternalDatabaseType(store.getString(OsdeConfig.EXTERNAL_DATABASE_TYPE));
            config.setExternalDatabaseHost(store.getString(OsdeConfig.EXTERNAL_DATABASE_HOST));
            config.setExternalDatabasePort(store.getString(OsdeConfig.EXTERNAL_DATABASE_PORT));
            config.setExternalDatabaseUsername(store
                    .getString(OsdeConfig.EXTERNAL_DATABASE_USERNAME));
            config.setExternalDatabasePassword(store
                    .getString(OsdeConfig.EXTERNAL_DATABASE_PASSWORD));
            config.setExternalDatabaseName(store.getString(OsdeConfig.EXTERNAL_DATABASE_NAME));
            config.setWorkDirectory(store.getString(OsdeConfig.WORK_DIRECTORY));
            config.setLoggerConfigFile(store.getString(OsdeConfig.LOGGER_CONFIG_FILE));
            config.setCompileJavaScript(store.getBoolean(OsdeConfig.COMPILE_JAVASCRIPT));
            config.setFirefoxLocation(store.getString(OsdeConfig.FIREFOX_LOCATION));
            return config;
        } catch (IOException e) {
            logger.error("Something went wrong while getting OSDE configurations.", e);
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            logger.error("Retrieving the preference values failed.", e);
            throw new IllegalStateException(e);
        }
    }    
    
    public OsdeConfig getDefaultOsdeConfiguration() {
        try {
            OsdeConfig config = new OsdeConfig();
            config.setDefaultCountry(store.getDefaultString(OsdeConfig.DEFAULT_COUNTRY));
            config.setDefaultLanguage(store.getDefaultString(OsdeConfig.DEFAULT_LANGUAGE));
            config.setDatabaseDir(store.getDefaultString(OsdeConfig.DATABASE_DIR));
            config.setDocsSiteMap(decodeSiteMap(store.getDefaultString(OsdeConfig.DOCS_SITE_MAP)));
            config.setJettyDir(store.getDefaultString(OsdeConfig.JETTY_DIR));
            config.setUseInternalDatabase(
                    store.getDefaultBoolean(OsdeConfig.USE_INTERNAL_DATABASE));
            config.setExternalDatabaseType(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_TYPE));
            config.setExternalDatabaseHost(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_HOST));
            config.setExternalDatabasePort(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_PORT));
            config.setExternalDatabaseUsername(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_USERNAME));
            config.setExternalDatabasePassword(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_PASSWORD));
            config.setExternalDatabaseName(
                    store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_NAME));
            config.setWorkDirectory(store.getDefaultString(OsdeConfig.WORK_DIRECTORY));
            config.setLoggerConfigFile(store.getDefaultString(OsdeConfig.LOGGER_CONFIG_FILE));
            config.setCompileJavaScript(store.getDefaultBoolean(OsdeConfig.COMPILE_JAVASCRIPT));
            config.setFirefoxLocation(store.getDefaultString(OsdeConfig.FIREFOX_LOCATION));
            return config;
        } catch (IOException e) {
            logger.error("Retrieving preference values failed.", e);
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            logger.error("Retrieving preference values failed.", e);
            throw new IllegalStateException(e);
        }
    }
    
    public void storePreferences(OsdeConfig config) {
        try {
            store.setValue(OsdeConfig.DEFAULT_COUNTRY, config.getDefaultCountry());
            store.setValue(OsdeConfig.DEFAULT_LANGUAGE, config.getDefaultLanguage());
            store.setValue(OsdeConfig.DATABASE_DIR, config.getDatabaseDir());
            store.setValue(OsdeConfig.DOCS_SITE_MAP, encodeSiteMap(config.getDocsSiteMap()));
            store.setValue(OsdeConfig.JETTY_DIR, config.getJettyDir());
            store.setValue(OsdeConfig.USE_INTERNAL_DATABASE, config.isUseInternalDatabase());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_HOST, config.getExternalDatabaseHost());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_PORT, config.getExternalDatabasePort());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_USERNAME, config
                    .getExternalDatabaseUsername());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_PASSWORD, config
                    .getExternalDatabasePassword());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_TYPE, config.getExternalDatabaseType());
            store.setValue(OsdeConfig.EXTERNAL_DATABASE_NAME, config.getExternalDatabaseName());
            store.setValue(OsdeConfig.WORK_DIRECTORY, config.getWorkDirectory());
            store.setValue(OsdeConfig.LOGGER_CONFIG_FILE, config.getLoggerConfigFile());
            store.setValue(OsdeConfig.COMPILE_JAVASCRIPT, config.isCompileJavaScript());
            store.setValue(OsdeConfig.FIREFOX_LOCATION, config.getFirefoxLocation());
        } catch (IOException e) {
            logger.error("Storing preference values failed.", e);
            throw new IllegalStateException(e);
        }
    }
    
    private String encodeSiteMap(Map<String, String> siteMap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(siteMap);
        out.flush();
        byte[] bytes = baos.toByteArray();
        byte[] encoded = Base64.encodeBase64(bytes);
        return new String(encoded, "UTF-8");
    }
    
    private Map<String, String> decodeSiteMap(String encodeSiteMap)
            throws IOException, ClassNotFoundException {
        if (encodeSiteMap != null && encodeSiteMap.length() > 0) {
            byte[] bytes = encodeSiteMap.getBytes("UTF-8");
            byte[] decoded = Base64.decodeBase64(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
            ObjectInputStream in = new ObjectInputStream(bais);

            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) in.readObject();
            return result;
        } else {
            return null;
        }
    }

}
