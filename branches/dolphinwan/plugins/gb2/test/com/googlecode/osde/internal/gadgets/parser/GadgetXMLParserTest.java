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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;

import com.googlecode.osde.internal.gadgets.model.MessageBundle;
import com.googlecode.osde.internal.gadgets.model.Module;
import com.googlecode.osde.internal.gadgets.model.Param;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link GadgetXMLParser}.
 *
 * @author Dolphin Wan (dolphin.wan@gmail.com)
 */
public class GadgetXMLParserTest {

    private GadgetXMLParser parser;
    private Reader reader;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        parser = new GadgetXMLParser();
        InputStream testSpec = getClass().getResourceAsStream("test_gadget.xml");
        reader = new InputStreamReader(testSpec, "utf-8");
    }

    @Test
    public final void testParse() throws ParserException {
        Module module = parser.parse(reader);
        assertNotNull(module);

        Module.ModulePrefs modulePrefs = module.getModulePrefs();
        assertNotNull(modulePrefs);

        assertEquals("title1", modulePrefs.getTitle());
        assertEquals("titleUrl1", modulePrefs.getTitleUrl());
        assertEquals("description1", modulePrefs.getDescription());
        assertEquals("author1", modulePrefs.getAuthor());
        assertEquals("authorEmail1", modulePrefs.getAuthorEmail());
        assertEquals("screenshot1", modulePrefs.getScreenshot());
        assertEquals("thumbnail1", modulePrefs.getThumbnail());
        assertEquals("78", modulePrefs.getExtraProperties().get("height"));

        List<Module.ModulePrefs.Require> requires = modulePrefs.getRequires();
        Module.ModulePrefs.Require require = requires.get(0);
        assertEquals("feature1", require.getFeature());

        List<Param> params = require.getParam();
        Param param = params.get(0);
        assertEquals("name1", param.getName());
        assertEquals("value1", param.getValue());

        List<Module.ModulePrefs.Optional> optionals = modulePrefs.getOptionals();
        Module.ModulePrefs.Optional optional = optionals.get(0);
        assertEquals("feature2", optional.getFeature());
        params = optional.getParam();
        param = params.get(0);
        assertEquals("name2", param.getName());
        assertEquals("value2", param.getValue());

        List<Module.ModulePrefs.Icon> icons = modulePrefs.getIcons();
        Module.ModulePrefs.Icon icon = icons.get(0);
        assertEquals("mode1", icon.getMode());
        assertEquals("type1", icon.getType());
        assertEquals("value3", icon.getValue());

        List<Module.ModulePrefs.Link> links = modulePrefs.getLinks();
        Module.ModulePrefs.Link link = links.get(0);
        assertEquals("href1", link.getHref());
        assertEquals("rel1", link.getRel());
        assertEquals("method1", link.getMethod());

        List<Module.ModulePrefs.Locale> locales = modulePrefs.getLocales();
        Module.ModulePrefs.Locale locale = locales.get(0);
        assertEquals("lang1", locale.getLang());
        assertEquals("country1", locale.getCountry());
        assertEquals("messages1", locale.getMessages());
        assertEquals("languageDirection1", locale.getLanguageDirection());
        List<MessageBundle.Msg> msgs = locale.getInlineMessages();
        MessageBundle.Msg msg = msgs.get(0);
        assertEquals("value4", msg.getContent());
        assertEquals("name3", msg.getName());
        assertEquals("desc1", msg.getDesc());

        List<Module.ModulePrefs.OAuth> oauths = modulePrefs.getOAuths();
        Module.ModulePrefs.OAuth oauth = oauths.get(0);
        List<Module.ModulePrefs.OAuth.Service> services = oauth.getService();
        Module.ModulePrefs.OAuth.Service service = services.get(0);
        assertEquals("name4", service.getName());
        Module.ModulePrefs.OAuth.Service.Request request = service.getRequest();
        assertEquals("url1", request.getUrl());
        assertEquals("method2", request.getMethod());
        assertEquals("paramLocation1", request.getParamLocation());
        Module.ModulePrefs.OAuth.Service.Access access = service.getAccess();
        assertEquals("url2", access.getUrl());
        assertEquals("method3", access.getMethod());
        assertEquals("paramLocation2", access.getParamLocation());
        Module.ModulePrefs.OAuth.Service.Authorization authorization = service.getAuthorization();
        assertEquals("url3", authorization.getUrl());

        List<Module.ModulePrefs.Preload> preloads = modulePrefs.getPreloads();
        Module.ModulePrefs.Preload preload = preloads.get(0);
        assertEquals("href2", preload.getHref());
        assertEquals("authz1", preload.getAuthz());
        assertTrue(preload.isSignOwner());
        assertTrue(preload.isSignViewer());
        assertEquals("views1", preload.getViews());
        assertEquals("oauthServiceName1", preload.getOauthServiceName());
        assertEquals("oauthTokenName1", preload.getOauthTokenName());
        assertEquals("oauthRequestToken1", preload.getOauthRequestToken());
        assertEquals("oauthRequestTokenSecret1", preload.getOauthRequestTokenSecret());

        List<Module.UserPref> userPrefs = module.getUserPref();
        Module.UserPref userPref = userPrefs.get(0);
        assertEquals("name5", userPref.getName());
        assertEquals("displayName1", userPref.getDisplayName());
        assertEquals("defaultValue1", userPref.getDefaultValue());
        assertEquals("required1", userPref.getRequired());
        assertEquals("datatype1", userPref.getDatatype());
        List<Module.UserPref.EnumValue> enumValues = userPref.getEnumValue();
        Module.UserPref.EnumValue enumValue = enumValues.get(0);
        assertEquals("value5", enumValue.getValue());
        assertEquals("displayValue1", enumValue.getDisplayValue());

        List<Module.Content> contents = module.getContent();
        Module.Content content = contents.get(0);
        assertEquals("type2", content.getType());
        assertEquals("href3", content.getHref());
        assertEquals("view1", content.getView());
        assertEquals(new BigInteger("123"), content.getPreferredWidth());
        assertEquals(new BigInteger("456"), content.getPreferredHeight());
        assertEquals("value6", content.getValue());
    }

}
