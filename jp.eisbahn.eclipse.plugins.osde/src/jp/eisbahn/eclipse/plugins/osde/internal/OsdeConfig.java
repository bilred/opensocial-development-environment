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
package jp.eisbahn.eclipse.plugins.osde.internal;

import java.util.Map;

public class OsdeConfig {
	
	public static final String DEFAULT_LANGUAGE = "language";
	public static final String DEFAULT_COUNTRY = "country";
	public static final String DATABASE_DIR = "database_dir";
	public static final String DOCS_SITE_MAP = "docs_site_map";
	
	private String defaultLanguage;
	private String defaultCountry;
	private String databaseDir;
	private Map<String, String> docsSiteMap;
	
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

}
