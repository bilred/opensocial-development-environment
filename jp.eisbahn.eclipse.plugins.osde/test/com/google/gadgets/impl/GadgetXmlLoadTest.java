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
import java.util.List;

import com.google.gadgets.GadgetXmlParser;
import com.google.gadgets.Module;
import com.google.gadgets.Param;
import com.google.gadgets.Module.ModulePrefs;
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

import junit.framework.TestCase;

public class GadgetXmlLoadTest extends TestCase {
	
	public void testGadgetXMLファイルの読み込み() throws Exception {
		File file = new File("test/test_gadget.xml");
		FileInputStream in = new FileInputStream(file);
		Module module = GadgetXmlParser.parse(in);
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

		in.close();
	}

}
