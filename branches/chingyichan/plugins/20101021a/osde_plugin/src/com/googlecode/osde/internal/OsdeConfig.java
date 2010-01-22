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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.osde.internal.profiling.FirefoxLocator;
import com.googlecode.osde.internal.utils.Logger;

/**
 * An immutable JavaBean of the OSDE configuration.
 */
public final class OsdeConfig {
    
    private static final Logger logger = new Logger(OsdeConfig.class);

    // Preference node names
    public static final String DEFAULT_LANGUAGE = "language";
    public static final String DEFAULT_COUNTRY = "country";
    public static final String DATABASE_DIR = "database_dir";
    public static final String DOCS_SITE_MAP = "docs_site_map";
    public static final String JETTY_DIR = "jetty_dir";
    public static final String USE_INTERNAL_DATABASE = "use_internal_database";
    public static final String EXTERNAL_DATABASE_TYPE = "external_database_type";
    public static final String EXTERNAL_DATABASE_HOST = "external_database_host";
    public static final String EXTERNAL_DATABASE_PORT = "external_database_port";
    public static final String EXTERNAL_DATABASE_USERNAME = "external_database_username";
    public static final String EXTERNAL_DATABASE_PASSWORD = "external_database_password";
    public static final String EXTERNAL_DATABASE_NAME = "external_database_name";
    public static final String WORK_DIRECTORY = "work_directory";
    public static final String LOGGER_CONFIG_FILE = "logger_config_file";
    public static final String COMPILE_JAVASCRIPT = "compile_javascript";
    public static final String FIREFOX_LOCATION = "firefox_location";

    // Default values
    public static final String DEFAULT_FIREFOX_LOCATION = new FirefoxLocator().getBinaryLocation();
    
    static interface PreferenceFetcher {
        
        public String get(OsdePreferencesModel model, String name);
        
        public boolean getBoolean(OsdePreferencesModel model, String name);
        
    }
    
    private OsdePreferencesModel model;
    private PreferenceFetcher prefFetcher;
    
    OsdeConfig(OsdePreferencesModel model, PreferenceFetcher fetcher){
        this.model = model;
        this.prefFetcher = fetcher;
    }

    public String getExternalDatabaseName() {
        return get(EXTERNAL_DATABASE_NAME);
    }

    public boolean isUseInternalDatabase() {
        return getBoolean(USE_INTERNAL_DATABASE);
    }

    public String getExternalDatabaseType() {
        return get(EXTERNAL_DATABASE_TYPE);
    }

    public String getExternalDatabaseHost() {
        return get(EXTERNAL_DATABASE_HOST);
    }

    public String getExternalDatabasePort() {
        return get(EXTERNAL_DATABASE_PORT);
    }

    public String getExternalDatabaseUsername() {
        return get(EXTERNAL_DATABASE_USERNAME);
    }

    public String getExternalDatabasePassword() {
        return get(EXTERNAL_DATABASE_PASSWORD);
    }

    public String getJettyDir() {
        return get(JETTY_DIR);
    }

    public Map<String, String> getDocsSiteMap() {
        try {
            return decodeSiteMap(get(DOCS_SITE_MAP));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new HashMap<String, String>();
    }

    public String getDatabaseDir() {
        return get(DATABASE_DIR);
    }

    public String getDefaultLanguage() {
        return get(DEFAULT_LANGUAGE);
    }

    public String getDefaultCountry() {
        return get(DEFAULT_COUNTRY);
    }

    public String getWorkDirectory() {
        return get(WORK_DIRECTORY);
    }

    public String getLoggerConfigFile() {
        return get(LOGGER_CONFIG_FILE);
    }

    public boolean isCompileJavaScript() {
        return getBoolean(COMPILE_JAVASCRIPT);
    }

    public String getFirefoxLocation() {
        return get(FIREFOX_LOCATION);
    }
    
    protected String get(String name) {
        return prefFetcher.get(model, name);
    }
    
    protected boolean getBoolean(String name) {
        return prefFetcher.getBoolean(model, name);
    }    

    static String encodeSiteMap(Map<String, String> siteMap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(siteMap);
        out.flush();
        byte[] bytes = baos.toByteArray();
        byte[] encoded = Base64.encodeBase64(bytes);
        return new String(encoded, "UTF-8");
    }
    
    static Map<String, String> decodeSiteMap(String encodeSiteMap)
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
