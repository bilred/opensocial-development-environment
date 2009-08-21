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

package jp.eisbahn.eclipse.plugins.osde.internal.preferences;

import java.util.Map;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
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
	
	private static Map<String, String> docsSiteMap;
	
	/**
	 * This getter method will not have synchronization issues because it is only called
	 * once in the lifetime of the whole plugin.
	 * @return docsSiteMap
	 */
	public static Map<String, String> getDocsSiteMap() {
		return docsSiteMap;
	}

	public static void setDocsSiteMap(Map<String, String> siteMap) {
		docsSiteMap = siteMap;
	}
}
