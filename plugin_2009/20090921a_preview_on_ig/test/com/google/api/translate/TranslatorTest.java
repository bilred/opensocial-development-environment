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

import com.google.api.translate.Language;
import com.google.api.translate.Translator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Provides test cases for testing Translator using Google Translate API
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class TranslatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testChineseToEnglishTranslation() {
		String str = Translator.translate("雷射", Language.CHINESE_TRADITIONAL, Language.ENGLISH);
		assertTrue("laser".equals(str.toLowerCase()));
		
		str = Translator.translate("軟體", Language.CHINESE_TRADITIONAL, Language.ENGLISH);
		assertTrue("software".equals(str.toLowerCase()));
	}
	
	@Test
	public void testEnglishToChineseTranlation() {
		String str = Translator.translate("Chiaroscuro", Language.ENGLISH, Language.CHINESE_TRADITIONAL);
		assertTrue("明暗對比".equals(str));
		
		str = Translator.translate("Test", Language.ENGLISH, Language.CHINESE_TRADITIONAL);
		assertTrue("測試".equals(str));
	}

	@After
	public void tearDown() throws Exception {
	}

}
