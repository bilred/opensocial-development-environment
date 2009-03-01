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
	}

}
