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
package com.google.gadgets.impl;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.List;

import com.google.gadgets.Module;
import com.google.gadgets.Param;
import com.google.gadgets.Module.Content;
import com.google.gadgets.Module.ModulePrefs;
import com.google.gadgets.Module.UserPref;
import com.google.gadgets.Module.ModulePrefs.Icon;
import com.google.gadgets.Module.ModulePrefs.Link;
import com.google.gadgets.Module.ModulePrefs.Locale;
import com.google.gadgets.Module.ModulePrefs.OAuth;
import com.google.gadgets.Module.ModulePrefs.Optional;
import com.google.gadgets.Module.ModulePrefs.Preload;
import com.google.gadgets.Module.ModulePrefs.Require;
import com.google.gadgets.Module.ModulePrefs.Locale.Msg;
import com.google.gadgets.Module.ModulePrefs.OAuth.Service;
import com.google.gadgets.Module.ModulePrefs.OAuth.Service.Access;
import com.google.gadgets.Module.ModulePrefs.OAuth.Service.Authorization;
import com.google.gadgets.Module.ModulePrefs.OAuth.Service.Request;
import com.google.gadgets.Module.UserPref.EnumValue;
import com.google.gadgets.parser.IParser;
import com.google.gadgets.parser.ParserFactory;
import com.google.gadgets.parser.ParserType;

import junit.framework.TestCase;

public class GadgetXmlLoadTest extends TestCase {
	
	private IParser target;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		target = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		target = null;
	}

	public void testParseGadgetXml() throws Exception {
		File file = new File("test/test_gadget.xml");
		FileInputStream in = new FileInputStream(file);
		Module module = (Module) target.parse(in);
		assertNotNull(module);
		ModulePrefs modulePrefs = module.getModulePrefs();
		assertNotNull(modulePrefs);
		assertEquals("title1", modulePrefs.getTitle());
		assertEquals("titleUrl1", modulePrefs.getTitleUrl());
		assertEquals("description1", modulePrefs.getDescription());
		assertEquals("author1", modulePrefs.getAuthor());
		assertEquals("authorEmail1", modulePrefs.getAuthorEmail());
		assertEquals("screenshot1", modulePrefs.getScreenshot());
		assertEquals("thumbnail1", modulePrefs.getThumbnail());
		List<Object> requireOrOptionalOrPreload = modulePrefs.getRequireOrOptionalOrPreload();
		Require require = (Require)requireOrOptionalOrPreload.get(0);
		assertEquals("feature1", require.getFeature());
		List<Param> params = require.getParam();
		Param param = params.get(0);
		assertEquals("name1", param.getName());
		assertEquals("value1", param.getValue());
		Optional optional = (Optional)requireOrOptionalOrPreload.get(1);
		assertEquals("feature2", optional.getFeature());
		params = optional.getParam();
		param = params.get(0);
		assertEquals("name2", param.getName());
		assertEquals("value2", param.getValue());
		Icon icon = (Icon)requireOrOptionalOrPreload.get(2);
		assertEquals("mode1", icon.getMode());
		assertEquals("type1", icon.getType());
		assertEquals("value3", icon.getValue());
		Link link = (Link)requireOrOptionalOrPreload.get(3);
		assertEquals("href1", link.getHref());
		assertEquals("rel1", link.getRel());
		assertEquals("method1", link.getMethod());
		Locale locale = (Locale)requireOrOptionalOrPreload.get(4);
		assertEquals("lang1", locale.getLang());
		assertEquals("country1", locale.getCountry());
		assertEquals("messages1", locale.getMessages());
		assertEquals("languageDirection1", locale.getLanguageDirection());
		List<Msg> msgs = locale.getMsg();
		Msg msg = msgs.get(0);
		assertEquals("value4", msg.getValue());
		assertEquals("name3", msg.getName());
		assertEquals("desc1", msg.getDesc());
		OAuth oauth = (OAuth)requireOrOptionalOrPreload.get(5);
		List<Service> services = oauth.getService();
		Service service = services.get(0);
		assertEquals("name4", service.getName());
		Request request = service.getRequest();
		assertEquals("url1", request.getUrl());
		assertEquals("method2", request.getMethod());
		assertEquals("paramLocation1", request.getParamLocation());
		Access access = service.getAccess();
		assertEquals("url2", access.getUrl());
		assertEquals("method3", access.getMethod());
		assertEquals("paramLocation2", access.getParamLocation());
		Authorization authorization = service.getAuthorization();
		assertEquals("url3", authorization.getUrl());
		Preload preload = (Preload)requireOrOptionalOrPreload.get(6);
		assertEquals("href2", preload.getHref());
		assertEquals("authz1", preload.getAuthz());
		assertTrue(preload.isSignOwner());
		assertTrue(preload.isSignViewer());
		assertEquals("views1", preload.getViews());
		assertEquals("oauthServiceName1", preload.getOauthServiceName());
		assertEquals("oauthTokenName1", preload.getOauthTokenName());
		assertEquals("oauthRequestToken1", preload.getOauthRequestToken());
		assertEquals("oauthRequestTokenSecret1", preload.getOauthRequestTokenSecret());
		List<UserPref> userPrefs = module.getUserPref();
		UserPref userPref = userPrefs.get(0);
		assertEquals("name5", userPref.getName());
		assertEquals("displayName1", userPref.getDisplayName());
		assertEquals("defaultValue1", userPref.getDefaultValue());
		assertEquals("required1", userPref.getRequired());
		assertEquals("datatype1", userPref.getDatatype());
		List<EnumValue> enumValues = userPref.getEnumValue();
		EnumValue enumValue = enumValues.get(0);
		assertEquals("value5", enumValue.getValue());
		assertEquals("displayValue1", enumValue.getDisplayValue());
		List<Content> contents = module.getContent();
		Content content = contents.get(0);
		assertEquals("type2", content.getType());
		assertEquals("href3", content.getHref());
		assertEquals("view1", content.getView());
		assertEquals(new BigInteger("123"), content.getPreferredWidth());
		assertEquals(new BigInteger("456"), content.getPreferredHeight());
		assertEquals("value6", content.getValue());
		in.close();
	}

}
