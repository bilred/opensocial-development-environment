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
package com.google.gadgets;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.xml.sax.SAXException;

public class GadgetXmlParser {
	
	protected Digester digester;
	
	public GadgetXmlParser() {
		super();
		init();
	}
	
	protected void init() {
		digester = new Digester();
		//
		digester.addRule("Module", new ObjectCreateRule(Module.class));
		//
		digester.addRule("Module/ModulePrefs", new ObjectCreateRule(Module.ModulePrefs.class));
		String[] attributeNames = new String[]{"title", "title_url", "description", "author", "author_email", "screenshot", "thumbnail"};
		String[] propertyNames = new String[]{"title", "titleUrl", "description", "author", "authorEmail", "screenshot", "thumbnail"};
		digester.addRule("Module/ModulePrefs", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs", new SetNextRule("setModulePrefs"));
		//
		digester.addRule("Module/ModulePrefs/Require", new ObjectCreateRule(Module.ModulePrefs.Require.class));
		attributeNames = new String[]{"feature"};
		propertyNames = new String[]{"feature"};
		digester.addRule("Module/ModulePrefs/Require", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Require", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/Require/Param", new ObjectCreateRule(Param.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		digester.addRule("Module/ModulePrefs/Require/Param", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Require/Param", new CallMethodRule("setValue", 0));
		digester.addRule("Module/ModulePrefs/Require/Param", new SetNextRule("addParam"));
		//
		digester.addRule("Module/ModulePrefs/Optional", new ObjectCreateRule(Module.ModulePrefs.Optional.class));
		attributeNames = new String[]{"feature"};
		propertyNames = new String[]{"feature"};
		digester.addRule("Module/ModulePrefs/Optional", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Optional", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/Optional/Param", new ObjectCreateRule(Param.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		digester.addRule("Module/ModulePrefs/Optional/Param", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Optional/Param", new CallMethodRule("setValue", 0));
		digester.addRule("Module/ModulePrefs/Optional/Param", new SetNextRule("addParam"));
		//
		digester.addRule("Module/ModulePrefs/Icon", new ObjectCreateRule(Module.ModulePrefs.Icon.class));
		attributeNames = new String[]{"mode", "type"};
		propertyNames = new String[]{"mode", "type"};
		digester.addRule("Module/ModulePrefs/Icon", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Icon", new CallMethodRule("setValue", 0));
		digester.addRule("Module/ModulePrefs/Icon", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/Link", new ObjectCreateRule(Module.ModulePrefs.Link.class));
		attributeNames = new String[]{"href", "rel", "method"};
		propertyNames = new String[]{"href", "rel", "method"};
		digester.addRule("Module/ModulePrefs/Link", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Link", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/Locale", new ObjectCreateRule(Module.ModulePrefs.Locale.class));
		attributeNames = new String[]{"lang", "country", "messages", "language_direction"};
		propertyNames = new String[]{"lang", "country", "messages", "languageDirection"};
		digester.addRule("Module/ModulePrefs/Locale", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Locale", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/Locale/msg", new ObjectCreateRule(Module.ModulePrefs.Locale.Msg.class));
		attributeNames = new String[]{"name", "desc"};
		propertyNames = new String[]{"name", "desc"};
		digester.addRule("Module/ModulePrefs/Locale/msg", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Locale/msg", new CallMethodRule("setValue", 0));
		digester.addRule("Module/ModulePrefs/Locale/msg", new SetNextRule("addMsg"));
		//
		digester.addRule("Module/ModulePrefs/OAuth", new ObjectCreateRule(Module.ModulePrefs.OAuth.class));
		digester.addRule("Module/ModulePrefs/OAuth", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/ModulePrefs/OAuth/Service", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		digester.addRule("Module/ModulePrefs/OAuth/Service", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/OAuth/Service", new SetNextRule("addService"));
		//
		digester.addRule("Module/ModulePrefs/OAuth/Service/Request", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Request.class));
		attributeNames = new String[]{"url", "method", "param_location"};
		propertyNames = new String[]{"url", "method", "paramLocation"};
		digester.addRule("Module/ModulePrefs/OAuth/Service/Request", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/OAuth/Service/Request", new SetNextRule("setRequest"));
		//
		digester.addRule("Module/ModulePrefs/OAuth/Service/Access", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Access.class));
		attributeNames = new String[]{"url", "method", "param_location"};
		propertyNames = new String[]{"url", "method", "paramLocation"};
		digester.addRule("Module/ModulePrefs/OAuth/Service/Access", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/OAuth/Service/Access", new SetNextRule("setAccess"));
		//
		digester.addRule("Module/ModulePrefs/OAuth/Service/Authorization", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Authorization.class));
		attributeNames = new String[]{"url"};
		propertyNames = new String[]{"url"};
		digester.addRule("Module/ModulePrefs/OAuth/Service/Authorization", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/OAuth/Service/Authorization", new SetNextRule("setAuthorization"));
		//
		digester.addRule("Module/ModulePrefs/Preload", new ObjectCreateRule(Module.ModulePrefs.Preload.class));
		attributeNames = new String[]{"href", "authz", "sign_owner", "sign_viewer", "views", "oauth_service_name", "oauth_token_name", "oauth_request_token", "oauth_request_token_secret"};
		propertyNames = new String[]{"href", "authz", "signOwner", "signViewer", "views", "oauthServiceName", "oauthTokenName", "oauthRequestToken", "oauthRequestTokenSecret"};
		digester.addRule("Module/ModulePrefs/Preload", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/ModulePrefs/Preload", new SetNextRule("addRequireOrOptionalOrPreload"));
		//
		digester.addRule("Module/UserPref", new ObjectCreateRule(Module.UserPref.class));
		attributeNames = new String[]{"name", "display_name", "default_value", "required", "datatype"};
		propertyNames = new String[]{"name", "displayName", "defaultValue", "required", "datatype"};
		digester.addRule("Module/UserPref", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/UserPref", new SetNextRule("addUserPref"));
		//
		digester.addRule("Module/UserPref/EnumValue", new ObjectCreateRule(Module.UserPref.EnumValue.class));
		attributeNames = new String[]{"value", "display_value"};
		propertyNames = new String[]{"value", "displayValue"};
		digester.addRule("Module/UserPref/EnumValue", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/UserPref/EnumValue", new SetNextRule("addEnumValue"));
		//
		digester.addRule("Module/Content", new ObjectCreateRule(Module.Content.class));
		attributeNames = new String[]{"type", "href", "view", "preferred_width", "preferred_height"};
		propertyNames = new String[]{"type", "href", "view", "preferredWidth", "preferredHeight"};
		digester.addRule("Module/Content", new SetPropertiesRule(attributeNames, propertyNames));
		digester.addRule("Module/Content", new CallMethodRule("setValue", 0));
		digester.addRule("Module/Content", new SetNextRule("addContent"));
	}

	public Module parse(InputStream in) throws IOException, SAXException {
		Module module = (Module)digester.parse(in);
		return module;
	}

}
