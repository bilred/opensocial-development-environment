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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LanguageTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testIsSupported() {
		assertTrue(Language.isSupported("en"));
		assertFalse(Language.isSupported("blah"));
	}
	
	@Test
	public void testGetLangCode() {
		String langCode = Language.FRENCH.getLangCode();
		assertTrue("fr".equals(langCode));
		
		langCode = Language.CHINESE_TRADITIONAL.getLangCode();
		assertTrue("zh-TW".equals(langCode));
	}
	
	@Test
	public void testFromString() {
		Language lang = Language.fromString("blah");
		assertTrue(lang == null);
		
		lang = Language.fromString("zh-TW");
		assertFalse(lang == null);
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
