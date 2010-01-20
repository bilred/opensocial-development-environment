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

import com.googlecode.osde.internal.gadgets.model.MessageBundle;
import com.googlecode.osde.internal.gadgets.model.Module;
import com.googlecode.osde.internal.gadgets.model.Param;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;

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
 */
public class GadgetXMLParser extends AbstractParser {

    protected void initialize(Digester digester) {
        digester.addRule("Module", new ObjectCreateRule(Module.class));

        digester.addFactoryCreate("Module/ModulePrefs",
                new ObjectFactory(Module.ModulePrefs.class));
        digester.addRule("Module/ModulePrefs", new SetNextRule("setModulePrefs"));

        digester.addRule("*/Param", new ObjectCreateRule(Param.class));
        digester.addRule("*/Param", new SetPropertiesRule("name", "name"));
        digester.addRule("*/Param", new CallMethodRule("setValue", 0));
        digester.addRule("*/Param", new SetNextRule("addParam"));

        digester.addFactoryCreate("Module/ModulePrefs/Require",
                new ObjectFactory(Module.ModulePrefs.Require.class));
        digester.addRule("Module/ModulePrefs/Require", new SetNextRule("addRequire"));

        digester.addFactoryCreate("Module/ModulePrefs/Optional",
                new ObjectFactory(Module.ModulePrefs.Optional.class));
        digester.addRule("Module/ModulePrefs/Optional", new SetNextRule("addOptional"));

        digester.addFactoryCreate("Module/ModulePrefs/Icon",
                new ObjectFactory(Module.ModulePrefs.Icon.class));
        digester.addRule("Module/ModulePrefs/Icon", new CallMethodRule("setValue", 0));
        digester.addRule("Module/ModulePrefs/Icon", new SetNextRule("addIcon"));

        digester.addFactoryCreate("Module/ModulePrefs/Link",
                new ObjectFactory(Module.ModulePrefs.Link.class));
        digester.addRule("Module/ModulePrefs/Link", new SetNextRule("addLink"));

        digester.addFactoryCreate("Module/ModulePrefs/Locale",
                new ObjectFactory(Module.ModulePrefs.Locale.class));
        digester.addRule("Module/ModulePrefs/Locale", new SetNextRule("addLocale"));

        digester.addFactoryCreate("Module/ModulePrefs/Locale/msg",
                new ObjectFactory(MessageBundle.Msg.class));
        digester.addRule("Module/ModulePrefs/Locale/msg", new CallMethodRule("setContent", 0));
        digester.addRule("Module/ModulePrefs/Locale/msg", new SetNextRule("addInlineMessage"));

        digester.addFactoryCreate("Module/ModulePrefs/OAuth",
                new ObjectFactory(Module.ModulePrefs.OAuth.class));
        digester.addRule("Module/ModulePrefs/OAuth", new SetNextRule("addOAuth"));

        digester.addFactoryCreate("Module/ModulePrefs/OAuth/Service",
                new ObjectFactory(Module.ModulePrefs.OAuth.Service.class));
        digester.addRule("Module/ModulePrefs/OAuth/Service", new SetNextRule("addService"));

        digester.addFactoryCreate("Module/ModulePrefs/OAuth/Service/Request",
                new ObjectFactory(Module.ModulePrefs.OAuth.Service.Request.class));
        digester.addRule("Module/ModulePrefs/OAuth/Service/Request", new SetNextRule("setRequest"));

        digester.addFactoryCreate("Module/ModulePrefs/OAuth/Service/Access",
                new ObjectFactory(Module.ModulePrefs.OAuth.Service.Access.class));
        digester.addRule("Module/ModulePrefs/OAuth/Service/Access", new SetNextRule("setAccess"));

        digester.addFactoryCreate("Module/ModulePrefs/OAuth/Service/Authorization",
                new ObjectFactory(Module.ModulePrefs.OAuth.Service.Authorization.class));
        digester.addRule("Module/ModulePrefs/OAuth/Service/Authorization",
                new SetNextRule("setAuthorization"));

        digester.addFactoryCreate("Module/ModulePrefs/Preload",
                new ObjectFactory(Module.ModulePrefs.Preload.class));
        digester.addRule("Module/ModulePrefs/Preload", new SetNextRule("addPreload"));

        digester.addFactoryCreate("Module/UserPref",
                new ObjectFactory(Module.UserPref.class));
        digester.addRule("Module/UserPref", new SetNextRule("addUserPref"));

        digester.addFactoryCreate("Module/UserPref/EnumValue",
                new ObjectFactory(Module.UserPref.EnumValue.class));
        digester.addRule("Module/UserPref/EnumValue", new SetNextRule("addEnumValue"));

        digester.addFactoryCreate("Module/Content",
                new ObjectFactory(Module.Content.class));
        digester.addRule("Module/Content", new CallMethodRule("setValue", 0));
        digester.addRule("Module/Content", new SetNextRule("addContent"));
    }

}
