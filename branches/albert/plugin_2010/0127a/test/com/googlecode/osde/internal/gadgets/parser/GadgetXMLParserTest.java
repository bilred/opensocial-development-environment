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

import java.io.File;
import java.io.FileWriter;

import com.googlecode.osde.internal.gadgets.model.Module;
import com.googlecode.osde.internal.gadgets.model.Module.ModulePrefs;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GadgetXMLParserTest extends TestCase {

    private GadgetXMLParser parser;
    private FileWriter fout;
    private File fin;

    public GadgetXMLParserTest(String testName) {
        super(testName);
    }

    @Before
    public void setUp() throws Exception {
        parser = new GadgetXMLParser();
        fout = new FileWriter("test.txt");
        fout.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        fout.write("<Module>");
        fout.write("  <ModulePrefs title=\"hello world example\">");
        fout.write("    <Require feature=\"opensocial-0.9\" />");
        fout.write("  </ModulePrefs>");
        fout.write("    <Content type=\"html\">");
        fout.write("      <![CDATA[");
        fout.write("        Hello, world!");
        fout.write("      ]]>");
        fout.write("    </Content>");
        fout.write("</Module>");
        fout.flush();
        fout.close();
    }

    @After
    public void tearDown() throws Exception {
        File file = new File("test.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public final void testInitialize() {
        assertFalse(parser == null);
    }

    @Test
    public final void testParse() throws ParserException {
        fin = new File("test.txt");
        assertTrue(fin.exists());
        Module module = null;
        module = (Module) parser.parse(fin);
        assertFalse(module == null);
        ModulePrefs modulePrefs = module.getModulePrefs();
        assertNotNull(modulePrefs);
        assertEquals("hello world example", modulePrefs.getTitle());
    }

}
