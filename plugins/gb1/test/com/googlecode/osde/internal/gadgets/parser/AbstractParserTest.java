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

package com.googlecode.osde.internal.gadgets.parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.junit.Test;

import static com.googlecode.osde.internal.gadgets.parser.AbstractParser.ObjectFactory.toCamelCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link AbstractParser}.
 *
 * @author dolphin.wan@gmail.com (Dolphin Wan)
 */
public class AbstractParserTest {
    @Test
    public void testToCamelCase() {
        assertNull(toCamelCase(null));
        assertEquals("", toCamelCase(""));
        assertEquals("usual", toCamelCase("usual"));
        assertEquals("usualWord", toCamelCase("usualWord"));
        assertEquals("usualWord", toCamelCase("usual_word"));
        assertEquals("usualWord", toCamelCase("usual_Word"));
        assertEquals("threeUsualWords", toCamelCase("three_usual_words"));
        assertEquals("aBeC", toCamelCase("a__be_c"));
    }

    @Test
    public void testA() throws ParserException {
        AbstractParser parser = new AbstractParser(){
            @Override
            protected void initialize(Digester digester) {
                digester.addFactoryCreate("a", new ObjectFactory(A.class));
            }
        };

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a b=\"3\"/>";

        A a = (A) parser.parse(new ByteArrayInputStream(xml.getBytes()));
        assertEquals("3", a.getExtraProperties().get("b"));
    }

    static class A implements AcceptExtraProperties {
        Map<String, String> map = new HashMap<String, String>();

        public Map<String, String> getExtraProperties() {
            return map;
        }
    }
}
