package com.googlecode.osde.internal;

import java.io.File;
import java.util.Locale;

import com.googlecode.osde.internal.common.JdkVersion;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

/**
 * Preference Initializer for OSDE.
 */
public class OsdePreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
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

}
