package com.googlecode.osde.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Preference Initializer for OSDE.
 */
public class OsdePreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        OsdePreferencesModel.initializeDefaultPreferences();
    }

}
