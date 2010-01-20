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

package com.googlecode.osde.internal.gadgets;

import java.math.BigInteger;

import com.googlecode.osde.internal.gadgets.model.Module;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link GadgetXmlSerializer}.
 *
 * @author dolphin.wan@gmail.com (Dolphin Wan)
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
