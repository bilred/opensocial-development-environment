package com.googlecode.osde.internal;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class OsdeConfigTest {

    OsdeConfig.PreferenceGetter getter;
    OsdeConfig config;

    @Before
    public void setUp() throws Exception{
        getter = createMock(OsdeConfig.PreferenceGetter.class);
        config = new OsdeConfig(null, getter);
    }


    @Test
    public void testPreferenceGetter() {
        OsdeConfig.PreferenceGetter getter = createMock(OsdeConfig.PreferenceGetter.class);
        OsdeConfig config = new OsdeConfig(null, getter);

        expect(getter.get(null, OsdeConfig.DATABASE_DIR)).andStubReturn("");
        expect(getter.getBoolean(null, OsdeConfig.COMPILE_JAVASCRIPT)).andStubReturn(true);

        replay(getter);
        config.getDatabaseDir();
        config.isCompileJavaScript();
        verify(getter);
    }

    @Test
    public void testGetters() throws Exception {

        expect(getter.get(null, OsdeConfig.DATABASE_DIR)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.DEFAULT_COUNTRY)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.DEFAULT_LANGUAGE)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.DOCS_SITE_MAP)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_HOST)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_NAME)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_PASSWORD)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_PORT)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_TYPE)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.EXTERNAL_DATABASE_USERNAME)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.FIREFOX_LOCATION)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.JETTY_DIR)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.LOGGER_CONFIG_FILE)).andReturn("").once();
        expect(getter.get(null, OsdeConfig.WORK_DIRECTORY)).andReturn("").once();
        expect(getter.getBoolean(null, OsdeConfig.COMPILE_JAVASCRIPT)).andReturn(true).once();
        expect(getter.getBoolean(null, OsdeConfig.USE_INTERNAL_DATABASE)).andReturn(true).once();

        replay(getter);
        config.getDatabaseDir();
        config.getDefaultCountry();
        config.getDefaultLanguage();
        config.getDocsSiteMap();
        config.getExternalDatabaseHost();
        config.getExternalDatabaseName();
        config.getExternalDatabasePassword();
        config.getExternalDatabasePort();
        config.getExternalDatabaseType();
        config.getExternalDatabaseUsername();
        config.getFirefoxLocation();
        config.getJettyDir();
        config.getLoggerConfigFile();
        config.getWorkDirectory();
        config.isCompileJavaScript();
        config.isUseInternalDatabase();
        verify(getter);
    }

}
