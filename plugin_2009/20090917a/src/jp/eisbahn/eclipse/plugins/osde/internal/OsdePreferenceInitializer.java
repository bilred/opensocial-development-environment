package jp.eisbahn.eclipse.plugins.osde.internal;

import java.io.File;
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
		String tmpdir = System.getProperty("java.io.tmpdir");
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
	}

}
