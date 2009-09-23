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
package com.google.gadgets.parser;

import com.google.gadgets.MessageBundle;
import com.google.gadgets.MessageBundle.Msg;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class MessageBundleXMLParserTest extends TestCase {

	private MessageBundleXMLParser msgBundleParser;
	private FileWriter fout;
	private File fin;
	
	public MessageBundleXMLParserTest (String testName) {
		super(testName);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		msgBundleParser = new MessageBundleXMLParser();
		fout = new FileWriter("test.txt");
		fout.write("<messagebundle>");
		fout.write("  <msg name=\"hello_world\">"); 
		fout.write("    Hello World."); 
		fout.write("  </msg>");
		fout.write("  <msg name=\"color\">Color</msg>");
		fout.write("  <msg name=\"red\">Red</msg>"); 
		fout.write("  <msg name=\"green\">Green</msg>");
		fout.write("  <msg name=\"blue\">Blue</msg>");
		fout.write("  <msg name=\"gray\">Gray</msg>");
		fout.write("  <msg name=\"purple\">Purple</msg>");
		fout.write("  <msg name=\"black\">Black</msg>");
		fout.write("</messagebundle>");
		fout.close();
	}

	@Test
	public void testParse() throws Exception {
		fin = new File("test.txt");
		assertFalse(msgBundleParser == null);
		MessageBundle msgBundle = (MessageBundle)msgBundleParser.parse(fin);
		assertFalse(msgBundle == null);
		List<Msg> messages = msgBundle.getMessages();
		assertEquals(messages.size(), 8);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		fin.deleteOnExit();
	}

}
