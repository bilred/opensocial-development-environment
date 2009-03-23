package jp.eisbahn.eclipse.plugins.osde.internal;

import java.util.Locale;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

public class OsdePreferenceInitializer extends AbstractPreferenceInitializer {

	public OsdePreferenceInitializer() {
		super();
	}

	@Override
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		Locale locale = Locale.getDefault();
		node.put(OsdeConfig.DEFAULT_COUNTRY, locale.getCountry());
		node.put(OsdeConfig.DEFAULT_LANGUAGE, locale.getLanguage());
		node.put(OsdeConfig.DATABASE_DIR, System.getProperty("java.io.tmpdir"));
		node.put(OsdeConfig.DOCS_SITE_MAP, "");
		node.put(OsdeConfig.JETTY_DIR, System.getProperty("java.io.tmpdir"));
		node.put(OsdeConfig.USE_INTERNAL_DATABASE, "true");
		node.put(OsdeConfig.EXTERNAL_DATABASE_TYPE, "MySQL");
		node.put(OsdeConfig.EXTERNAL_DATABASE_HOST, "localhost");
		node.put(OsdeConfig.EXTERNAL_DATABASE_PORT, "3306");
		node.put(OsdeConfig.EXTERNAL_DATABASE_USERNAME, "");
		node.put(OsdeConfig.EXTERNAL_DATABASE_PASSWORD, "");
		node.put(OsdeConfig.EXTERNAL_DATABASE_NAME, "osde");
	}

}
