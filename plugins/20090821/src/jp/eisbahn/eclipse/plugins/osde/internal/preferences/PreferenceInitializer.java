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

import java.io.File;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

/**
 * Class used to initialize default preference values.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Sets default values for all configuration variables.
	 * Note that this function also accesses outside files for storing database related information
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		Locale locale = Locale.getDefault();
		store.setDefault(PreferenceConstants.DEFAULT_COUNTRY, locale.getCountry());
		store.setDefault(PreferenceConstants.DEFAULT_LANGUAGE, locale.getLanguage());
		
		store.setDefault(PreferenceConstants.USE_INTERNAL_DATABASE, true);
		
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_TYPE, "MySQL");
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_HOST, "localhost");
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_PORT, 3306);
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_USERNAME, "");
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_PASSWORD, "");
		store.setDefault(PreferenceConstants.EXTERNAL_DATABASE_NAME, "osde");
		
		String tmpdir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpdir, "osde_db");
		file.mkdirs();
		store.setDefault(PreferenceConstants.DATABASE_DIR, file.getAbsolutePath());
		store.setDefault(PreferenceConstants.DOCS_SITE_MAP, "");
		file = new File(tmpdir, "osde_jetty");
		file.mkdirs();
		store.setDefault(PreferenceConstants.JETTY_DIR, file.getAbsolutePath());
	}

}
