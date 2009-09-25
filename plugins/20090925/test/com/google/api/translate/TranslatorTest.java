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
package com.google.api.translate;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.api.translate.Language;
import com.google.api.translate.Translator;
import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Provides test cases for testing Translator using Google Translate API
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class TranslatorTest {

	private Translator translator;
	
	@Before
	public void setUp() throws Exception {
		translator = new Translator();
		translator.setReferrer("http://code.google.com/p/google-translate-api-java-client/");
	}
	
	@Test
	public void testSetAndGetReferrer() {
		assertTrue(translator.getReferrer().equals("http://code.google.com/p/google-translate-api-java-client/"));
	}
	
	@Test
	public void testChineseToEnglishTranslation() {
		String str = translator.translate("雷射", Language.CHINESE_TRADITIONAL, Language.ENGLISH);
		assertTrue("laser".equals(str.toLowerCase()));
		
		str = translator.translate("軟體", Language.CHINESE_TRADITIONAL, Language.ENGLISH);
		assertTrue("software".equals(str.toLowerCase()));
	}
	
	@Test
	public void testEnglishToChineseTranlation() {
		String str = translator.translate("Chiaroscuro", Language.ENGLISH, Language.CHINESE_TRADITIONAL);
		assertTrue("明暗對比".equals(str));
		
		str = translator.translate("Test", Language.ENGLISH, Language.CHINESE_TRADITIONAL);
		assertTrue("測試".equals(str));
	}

	@Test
	public void testOneToManyTranslations() {
		ArrayList<String> results = translator.translate("hello world", Language.ENGLISH,
														 Language.ITALIAN, Language.FRENCH);
		assertEquals(results.size(), 2);
		assertTrue("ciao a tutti".equals(results.get(0)));
		assertTrue("Bonjour tout le monde".equals(results.get(1)));
	}
	
	@Test
	public void testMultipleStringsTranslations() {
		ArrayList<String> results = translator.translate(Language.ENGLISH, Language.ITALIAN, "hello world", "goodbye");
		assertEquals(results.size(), 2);
		assertTrue("ciao a tutti".equals(results.get(0)));
		assertTrue("arrivederci".equals(results.get(1)));
	}
	
	@Test
	public void testOpenConnection() {
		try {
			Translator localTranslator = new Translator();
			URLConnection connection = (URLConnection) PrivateAccessor.getField(translator, "connection");
			connection = null;
			assertTrue(connection == null);
			PrivateAccessor.invoke(localTranslator, "openConnection", new Class[]{String.class}, new Object[]{"http://localhost:1234"});
			assertTrue(connection == null);
			PrivateAccessor.invoke(localTranslator, "openConnection", new Class[]{String.class}, new Object[]{"malformed url"});
			assertTrue(connection == null);
		} catch (Throwable e) {
			fail();
		}
	}
	
	@Test
	public void testEncodeQueryText() {
		Translator localTranslator = new Translator();
		StringBuilder builder = new StringBuilder("");
		String text = "|"; // an empty space
		
		try {
			String encoding = "UTF-8";
			PrivateAccessor.invoke(localTranslator, "encodeAndConstructQueryText",
								   new Class[]{StringBuilder.class, String.class, String.class},
								   new Object[]{builder, text, encoding});
			assertTrue(builder.toString().equals("&q=%7C"));
			
			builder = new StringBuilder("");
			encoding = "blah";
			PrivateAccessor.invoke(localTranslator, "encodeAndConstructQueryText",
						   		   new Class[]{StringBuilder.class, String.class, String.class},
						   		   new Object[]{builder, text, encoding});
			assertTrue(builder.toString().equals("&q="));
			
		} catch (Throwable e) {
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRetrieveJSONResultsException() {
		Translator localTranslator = new Translator();
		List<String> results = null;
		try {
			results = (ArrayList<String>) PrivateAccessor.invoke(localTranslator, "retrieveMultipleResultsFromJSONResponse",
								   						  	     new Class[]{String.class}, new Object[]{"blah blah"});
		} catch (Throwable e) {
			fail();
		}
		assertTrue(results == null);
	}

	@After
	public void tearDown() throws Exception {
	}

}
