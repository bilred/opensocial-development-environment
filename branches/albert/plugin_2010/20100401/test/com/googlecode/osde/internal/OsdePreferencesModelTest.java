package com.googlecode.osde.internal;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.osde.internal.utils.MapUtil;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class OsdePreferencesModelTest {

    @Test
    public void testGetOsdeConfiguration() throws Exception {
        IPreferenceStore store = createMock(IPreferenceStore.class);
        OsdePreferencesModel model = new OsdePreferencesModel(store);

        expect(store.getString(OsdeConfig.DEFAULT_COUNTRY)).andStubReturn("");
        expect(store.getBoolean(OsdeConfig.USE_INTERNAL_DATABASE)).andStubReturn(Boolean.TRUE);

        replay(store);
        model.getOsdeConfiguration().getDefaultCountry();
        model.getOsdeConfiguration().isUseInternalDatabase();

        verify(store);
    }

    @Test
    public void testGetDefaultOsdeConfiguration() throws Exception {
        IPreferenceStore store = createMock(IPreferenceStore.class);
        OsdePreferencesModel model = new OsdePreferencesModel(store);

        expect(store.getDefaultString(OsdeConfig.DEFAULT_COUNTRY)).andStubReturn("");
        expect(store.getDefaultBoolean(OsdeConfig.USE_INTERNAL_DATABASE))
                .andStubReturn(Boolean.TRUE);

        replay(store);
        model.getDefaultOsdeConfiguration().getDefaultCountry();
        model.getDefaultOsdeConfiguration().isUseInternalDatabase();

        verify(store);
    }

    @Test
    public void testStore() throws Exception {
        IPreferenceStore store = createMock(IPreferenceStore.class);
        OsdePreferencesModel model = new OsdePreferencesModel(store);
        store.setValue("foo", false);
        store.setValue("abc", "def");

        Map<String, String> m = new HashMap<String, String>();
        m.put("mapKey", "mapValue");
        store.setValue("aMap", MapUtil.toString(m));

        replay(store);

        model.store("foo", false);
        model.store("abc", "def");
        model.store("aMap", m);
        verify(store);
    }

    @Test
    public void testStoreMap() throws Exception {
        IPreferenceStore store = createMock(IPreferenceStore.class);
        OsdePreferencesModel model = new OsdePreferencesModel(store);
        store.setValue("booleanKey", true);
        store.setValue("stringKey", "aString");

        replay(store);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("booleanKey", true);
        map.put("stringKey", "aString");
        model.store(map);

        verify(store);

    }
}
