package com.googlecode.osde.internal.gadgets;

import java.math.BigInteger;

import com.googlecode.osde.internal.gadgets.model.Module;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Dolphin Wan
 */
public class GadgetXmlSerializerTest {
    private Module module;
    private Module.ModulePrefs prefs;

    @Before
    public void setUp() {
        module = new Module();
        prefs = new Module.ModulePrefs();
        module.setModulePrefs(prefs);
    }

    @Test
    public void testSerializeExtraProperties() {
        prefs.getExtraProperties().put("abc", "12");
        prefs.getExtraProperties().put("def", "34");

        String output = GadgetXmlSerializer.serialize(module);
        assertTrue(output.contains("abc=\"12\""));
        assertTrue(output.contains("def=\"34\""));
    }

    @Test
    public void testSerializeContent() {
        Module.Content content = new Module.Content();
        content.setPreferredHeight(new BigInteger("1000"));
        content.setPreferredWidth(new BigInteger("700"));
        module.addContent(content);

        String output = GadgetXmlSerializer.serialize(module);
        assertTrue(output.contains("preferred_height=\"1000\""));
        assertTrue(output.contains("preferred_width=\"700\""));
    }
}
