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

import java.util.ArrayList;

import com.google.api.translate.Language;
import com.google.api.translate.Translator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	public void testThreadSafty() {
		final int NUM_THREADS = 1000;
		for (int i = 0; i < NUM_THREADS; ++i) {
			Thread translatorConsumer = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep((int)(Math.random() * 2000));
					} catch (InterruptedException e) {
						System.err.print("Sleeping consumer interrupted");
						e.printStackTrace();
					}
					String translation = translator.translate("peace", Language.ENGLISH, Language.FRENCH);
					assertFalse(translation == null);
					assertTrue("paix".equals(translation));
				}
			});
			translatorConsumer.start();
		}
		
		try {
			// wait for all consumer threads to terminate
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.err.println("testThreadSafety method interrupted when waiting for consumers");
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
