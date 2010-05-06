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

import java.io.StringReader;

import com.googlecode.osde.internal.gadgets.model.MessageBundle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link MessageBundleXMLParser}.
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 * @author Dolphin Wan (dolphin.wan@gmail.com)
 */
public class MessageBundleXMLParserTest {

    private MessageBundleXMLParser msgBundleParser;
    private StringReader reader;

    @Before
    public void setUp() throws Exception {
        msgBundleParser = new MessageBundleXMLParser();
        reader = new StringReader("<messagebundle>"
                + "  <msg name=\"hello_world\">"
                + "    Hello World."
                + "  </msg>"
                + "  <msg name=\"color\">Color</msg>"
                + "  <msg name=\"red\">Red</msg>"
                + "  <msg name=\"green\">Green</msg>"
                + "  <msg name=\"blue\">Blue</msg>"
                + "  <msg name=\"gray\">Gray</msg>"
                + "  <msg name=\"purple\">Purple</msg>"
                + "  <msg name=\"black\">Black</msg>"
                + "</messagebundle>");
    }

    @Test
    public void testParse() throws Exception {
        MessageBundle msgBundle = msgBundleParser.parse(reader);
        assertNotNull(msgBundle);
        assertEquals(8, msgBundle.getMessages().size());
    }
}
