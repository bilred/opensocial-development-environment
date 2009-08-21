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

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */

public class OsdePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public OsdePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configurations for OSDE, Shindig, Database");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {		
		addField(new StringFieldEditor(PreferenceConstants.DEFAULT_COUNTRY,
									   "Country",
									   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DEFAULT_LANGUAGE,
				   					   "Language",
				   					   getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.USE_INTERNAL_DATABASE,
				   						"Use internal database",
				   						getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_TYPE,
				   					   "External database type",
				   					   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_HOST,
				   					   "Database host",
				   					   getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_PORT,
				   						"Database port",
				   						getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_NAME,
				   					   "Database user name",
				   					   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.EXTERNAL_DATABASE_PASSWORD,
				   					   "Database password",
				   					   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DATABASE_DIR,
				   					   "Database directory",
				   					   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.JETTY_DIR,
				   					   "Jetty directory",
				   					   getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DOCS_SITE_MAP,
				   					   "Docs site map",
				   					   getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}