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

import java.io.IOException;
import java.io.InputStream;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.xml.sax.SAXException;

import com.google.gadgets.Module;
import com.google.gadgets.Param;

/**
 * Parser class for parsing gadgets.xml file using Apache's Digester
 * Refer to http://code.google.com/intl/zh-TW/apis/gadgets/docs/reference.html
 * for complete information of gadgets XML specifications.
 * Since we have separate files for message bundles, a separate parser is implemented
 * for parsing message bundle files. Refer to MessageBundleXMLParser.java for more details.
 * 
 * Note:
 * Attribute names correspond to attributes in gadgets XML specifications
 * Property names correspond to fields of Java beans that will be populated after parsing
 * For example, for ModulePrefs element in the XML file, it has attribute "author_email".
 * This corresponds to <ModulePrefs author_email="...">. The corresponding field is "authorEmail"
 * defined in the variable propertyNames. The field is located in class ModulePrefs in Module.java.
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class GadgetXMLParser extends Digester implements IParser {
	
	public GadgetXMLParser() {
		super();
		initialize();
	}
	
	protected void initialize() {
		addRule("Module", new ObjectCreateRule(Module.class));

		addRule("Module/ModulePrefs", new ObjectCreateRule(Module.ModulePrefs.class));
		String[] attributeNames = new String[]{"title", "title_url", "description", "author", "author_email", "screenshot", "thumbnail"};
		String[] propertyNames = new String[]{"title", "titleUrl", "description", "author", "authorEmail", "screenshot", "thumbnail"};
		addRule("Module/ModulePrefs", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs", new SetNextRule("setModulePrefs"));

		addRule("Module/ModulePrefs/Require", new ObjectCreateRule(Module.ModulePrefs.Require.class));
		attributeNames = new String[]{"feature"};
		propertyNames = new String[]{"feature"};
		addRule("Module/ModulePrefs/Require", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Require", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/Require/Param", new ObjectCreateRule(Param.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		addRule("Module/ModulePrefs/Require/Param", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Require/Param", new CallMethodRule("setValue", 0));
		addRule("Module/ModulePrefs/Require/Param", new SetNextRule("addParam"));

		addRule("Module/ModulePrefs/Optional", new ObjectCreateRule(Module.ModulePrefs.Optional.class));
		attributeNames = new String[]{"feature"};
		propertyNames = new String[]{"feature"};
		addRule("Module/ModulePrefs/Optional", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Optional", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/Optional/Param", new ObjectCreateRule(Param.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		addRule("Module/ModulePrefs/Optional/Param", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Optional/Param", new CallMethodRule("setValue", 0));
		addRule("Module/ModulePrefs/Optional/Param", new SetNextRule("addParam"));

		addRule("Module/ModulePrefs/Icon", new ObjectCreateRule(Module.ModulePrefs.Icon.class));
		attributeNames = new String[]{"mode", "type"};
		propertyNames = new String[]{"mode", "type"};
		addRule("Module/ModulePrefs/Icon", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Icon", new CallMethodRule("setValue", 0));
		addRule("Module/ModulePrefs/Icon", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/Link", new ObjectCreateRule(Module.ModulePrefs.Link.class));
		attributeNames = new String[]{"href", "rel", "method"};
		propertyNames = new String[]{"href", "rel", "method"};
		addRule("Module/ModulePrefs/Link", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Link", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/Locale", new ObjectCreateRule(Module.ModulePrefs.Locale.class));
		attributeNames = new String[]{"lang", "country", "messages", "language_direction"};
		propertyNames = new String[]{"lang", "country", "messages", "languageDirection"};
		addRule("Module/ModulePrefs/Locale", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Locale", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/OAuth", new ObjectCreateRule(Module.ModulePrefs.OAuth.class));
		addRule("Module/ModulePrefs/OAuth", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/ModulePrefs/OAuth/Service", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.class));
		attributeNames = new String[]{"name"};
		propertyNames = new String[]{"name"};
		addRule("Module/ModulePrefs/OAuth/Service", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/OAuth/Service", new SetNextRule("addService"));

		addRule("Module/ModulePrefs/OAuth/Service/Request", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Request.class));
		attributeNames = new String[]{"url", "method", "param_location"};
		propertyNames = new String[]{"url", "method", "paramLocation"};
		addRule("Module/ModulePrefs/OAuth/Service/Request", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/OAuth/Service/Request", new SetNextRule("setRequest"));

		addRule("Module/ModulePrefs/OAuth/Service/Access", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Access.class));
		attributeNames = new String[]{"url", "method", "param_location"};
		propertyNames = new String[]{"url", "method", "paramLocation"};
		addRule("Module/ModulePrefs/OAuth/Service/Access", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/OAuth/Service/Access", new SetNextRule("setAccess"));

		addRule("Module/ModulePrefs/OAuth/Service/Authorization", new ObjectCreateRule(Module.ModulePrefs.OAuth.Service.Authorization.class));
		attributeNames = new String[]{"url"};
		propertyNames = new String[]{"url"};
		addRule("Module/ModulePrefs/OAuth/Service/Authorization", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/OAuth/Service/Authorization", new SetNextRule("setAuthorization"));

		addRule("Module/ModulePrefs/Preload", new ObjectCreateRule(Module.ModulePrefs.Preload.class));
		attributeNames = new String[]{"href", "authz", "sign_owner", "sign_viewer", "views", "oauth_service_name", "oauth_token_name", "oauth_request_token", "oauth_request_token_secret"};
		propertyNames = new String[]{"href", "authz", "signOwner", "signViewer", "views", "oauthServiceName", "oauthTokenName", "oauthRequestToken", "oauthRequestTokenSecret"};
		addRule("Module/ModulePrefs/Preload", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/ModulePrefs/Preload", new SetNextRule("addRequireOrOptionalOrPreload"));

		addRule("Module/UserPref", new ObjectCreateRule(Module.UserPref.class));
		attributeNames = new String[]{"name", "display_name", "default_value", "required", "datatype"};
		propertyNames = new String[]{"name", "displayName", "defaultValue", "required", "datatype"};
		addRule("Module/UserPref", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/UserPref", new SetNextRule("addUserPref"));

		addRule("Module/UserPref/EnumValue", new ObjectCreateRule(Module.UserPref.EnumValue.class));
		attributeNames = new String[]{"value", "display_value"};
		propertyNames = new String[]{"value", "displayValue"};
		addRule("Module/UserPref/EnumValue", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/UserPref/EnumValue", new SetNextRule("addEnumValue"));

		addRule("Module/Content", new ObjectCreateRule(Module.Content.class));
		attributeNames = new String[]{"type", "href", "view", "preferred_width", "preferred_height"};
		propertyNames = new String[]{"type", "href", "view", "preferredWidth", "preferredHeight"};
		addRule("Module/Content", new SetPropertiesRule(attributeNames, propertyNames));
		addRule("Module/Content", new CallMethodRule("setValue", 0));
		addRule("Module/Content", new SetNextRule("addContent"));
	}

	@Override
	public Module parse(InputStream in) throws IOException, SAXException {
		try {
			Module module = (Module)super.parse(in);
			return module;
		} catch (IOException ioe) {
			Logging.error("Error loading gadget XML file:");
			Logging.error(ioe.toString());
		} catch (SAXException saxe) {
			Logging.error("Error parsing gadget XML file:");
			Logging.error(saxe.toString());
			// TODO: mark parsing errors in the editor instead of failing
		} finally {
			clear();
		}
		return null;
	}

}