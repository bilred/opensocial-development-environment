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

import java.util.Map;

/**
 * A JavaBean of the OSDE configuration.
 */
public class OsdeConfig {

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

	private String defaultLanguage;
	private String defaultCountry;
	private String databaseDir;
	private Map<String, String> docsSiteMap;
	private String jettyDir;
	private boolean useInternalDatabase;
	private String externalDatabaseType;
	private String externalDatabaseHost;
	private String externalDatabasePort;
	private String externalDatabaseUsername;
	private String externalDatabasePassword;
	private String externalDatabaseName;
	private String workDirectory;
	private String loggerConfigFile;
	private boolean compileJavaScript;

	public String getExternalDatabaseName() {
		return externalDatabaseName;
	}

	public void setExternalDatabaseName(String externalDatabaseName) {
		this.externalDatabaseName = externalDatabaseName;
	}

	public boolean isUseInternalDatabase() {
		return useInternalDatabase;
	}

	public void setUseInternalDatabase(boolean useInternalDatabase) {
		this.useInternalDatabase = useInternalDatabase;
	}

	public String getExternalDatabaseType() {
		return externalDatabaseType;
	}

	public void setExternalDatabaseType(String externalDatabaseType) {
		this.externalDatabaseType = externalDatabaseType;
	}

	public String getExternalDatabaseHost() {
		return externalDatabaseHost;
	}

	public void setExternalDatabaseHost(String externalDatabaseHost) {
		this.externalDatabaseHost = externalDatabaseHost;
	}

	public String getExternalDatabasePort() {
		return externalDatabasePort;
	}

	public void setExternalDatabasePort(String externalDatabasePort) {
		this.externalDatabasePort = externalDatabasePort;
	}

	public String getExternalDatabaseUsername() {
		return externalDatabaseUsername;
	}

	public void setExternalDatabaseUsername(String externalDatabaseUsername) {
		this.externalDatabaseUsername = externalDatabaseUsername;
	}

	public String getExternalDatabasePassword() {
		return externalDatabasePassword;
	}

	public void setExternalDatabasePassword(String externalDatabasePassword) {
		this.externalDatabasePassword = externalDatabasePassword;
	}

	public String getJettyDir() {
		return jettyDir;
	}

	public void setJettyDir(String jettyDir) {
		this.jettyDir = jettyDir;
	}

	public Map<String, String> getDocsSiteMap() {
		return docsSiteMap;
	}

	public void setDocsSiteMap(Map<String, String> docsSiteMap) {
		this.docsSiteMap = docsSiteMap;
	}

	public String getDatabaseDir() {
		return databaseDir;
	}

	public void setDatabaseDir(String databaseDir) {
		this.databaseDir = databaseDir;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getWorkDirectory() {
		return workDirectory;
	}

	public void setWorkDirectory(String workDirectory) {
		this.workDirectory = workDirectory;
	}
	
	public String getLoggerConfigFile() {
		return loggerConfigFile;
	}

	public void setLoggerConfigFile(String loggerConfigFile) {
		this.loggerConfigFile = loggerConfigFile;
	}

	public boolean isCompileJavaScript() {
		return compileJavaScript;
	}

	public void setCompileJavaScript(boolean compileJavaScript) {
		this.compileJavaScript = compileJavaScript;
	}
}
