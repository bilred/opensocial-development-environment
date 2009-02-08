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
package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;

import com.google.gadgets.GadgetFeatureType;
import com.google.gadgets.Module;
import com.google.gadgets.Module.Content;
import com.google.gadgets.Module.ModulePrefs;
import com.google.gadgets.Module.UserPref;
import com.google.gadgets.Module.ModulePrefs.Locale;
import com.google.gadgets.Module.ModulePrefs.Locale.Msg;
import com.google.gadgets.Module.UserPref.EnumValue;

public class GadgetXmlSerializer {

	public static String serialize(Module module) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<Module>\n");
		sb.append("    <ModulePrefs");
		sb.append(createModulePrefsAttributes(module));
		sb.append(createRequires(module));
		sb.append(createLocales(module));
		sb.append("    </ModulePrefs>\n");
		sb.append(createUserPrefs(module));
		sb.append(createContents(module));
		sb.append("</Module>");
		return sb.toString();
	}
	
	private static String createContents(Module module) {
		StringBuilder sb = new StringBuilder();
		List<Content> contents = module.getContent();
		for (Content content : contents) {
			sb.append("    <Content");
			createAttribute("href", content.getHref(), sb);
			createAttribute("type", content.getType(), sb);
			createAttribute("view", content.getView(), sb);
			String body = content.getValue();
			if (StringUtils.isNotEmpty(body)) {
				sb.append("><![CDATA[");
				sb.append(body);
				sb.append("]]></Content>\n");
			} else {
				sb.append(" />\n");
			}
		}
		return sb.toString();
	}

	private static String createUserPrefs(Module module) {
		StringBuilder sb = new StringBuilder();
		List<UserPref> userPrefs = module.getUserPref();
		for (UserPref userPref : userPrefs) {
			sb.append("    <UserPref");
			createAttribute("name", userPref.getName(), sb);
			createAttribute("display_name", userPref.getDisplayName(), sb);
			createAttribute("datatype", userPref.getDatatype(), sb);
			createAttribute("default_value", userPref.getDefaultValue(), sb);
			String required = userPref.getRequired();
			if (StringUtils.isNotEmpty(required) && "true".equalsIgnoreCase(required)) {
				sb.append(" required=\"true\"");
			}
			List<EnumValue> enumValues = userPref.getEnumValue();
			if (enumValues != null && !enumValues.isEmpty()) {
				sb.append(">\n");
				for (EnumValue enumValue : enumValues) {
					sb.append("        <EnumValue");
					createAttribute("value", enumValue.getValue(), sb);
					createAttribute("display_value", enumValue.getDisplayValue(), sb);
					sb.append(" />\n");
				}
				sb.append("    </UserPref>\n");
			} else {
				sb.append(" />\n");
			}
		}
		return sb.toString();
	}

	private static Object createLocales(Module module) {
		StringBuilder sb = new StringBuilder();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (JAXBElement<?> element : elements) {
			Object value = element.getValue();
			if (value instanceof Locale) {
				Locale locale = (Locale)value;
				sb.append("        <Locale");
				createAttribute("lang", locale.getLang(), sb);
				createAttribute("country", locale.getCountry(), sb);
				createAttribute("messages", locale.getMessages(), sb);
				List<Msg> msgs = locale.getMsg();
				if (msgs != null && !msgs.isEmpty()) {
					sb.append(">\n");
					for (Msg msg : msgs) {
						sb.append("            <msg name=\"" + msg.getName() + "\">" + escape(msg.getValue()) + "</msg>\n");
					}
					sb.append("        </Locale>\n");
				} else {
					sb.append(" />\n");
				}
			}
		}
		return sb.toString();
	}

	private static String createRequires(Module module) {
		StringBuilder sb = new StringBuilder();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<JAXBElement<?>> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (JAXBElement<?> element : elements) {
			Object value = element.getValue();
			if (value instanceof GadgetFeatureType) {
				GadgetFeatureType type = (GadgetFeatureType)value;
				String featureRealName = type.getFeature();
				sb.append("        <Require feature=\"" + featureRealName + "\" />\n");
			}
		}
		return sb.toString();
	}

	private static String createModulePrefsAttributes(Module module) {
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<String> list = new ArrayList<String>();
		createModulePrefsAttribute("title", modulePrefs.getTitle(), list);
		createModulePrefsAttribute("title_url", modulePrefs.getTitleUrl(), list);
		createModulePrefsAttribute("author", modulePrefs.getAuthor(), list);
		createModulePrefsAttribute("author_email", modulePrefs.getAuthorEmail(), list);
		createModulePrefsAttribute("description", modulePrefs.getDescription(), list);
		createModulePrefsAttribute("screenshot", modulePrefs.getScreenshot(), list);
		createModulePrefsAttribute("thumbnail", modulePrefs.getThumbnail(), list);
		StringBuilder sb = new StringBuilder();
		for (String line : list) {
			sb.append("\n" + line);
		}
		sb.append(">\n");
		return sb.toString();
	}
	
	private static void createModulePrefsAttribute(String name, String value, List<String> list) {
		if (StringUtils.isNotEmpty(value)) {
			list.add("            " + name + "=\"" + escape(value) + "\"");
		}
	}
	
	private static String escape(String value) {
		String result = value.replace("<", "&lt;");
		result = value.replace("&", "&amp;");
		result = value.replace(">", "&gt;");
		result = value.replace("\"", "&quot;");
		result = value.replace("'", "&apos;");
		return result;
	}
	
	private static void createAttribute(String name, String value, StringBuilder sb) {
		if (StringUtils.isNotEmpty(value)) {
			sb.append(" " + name + "=\"" + escape(value) + "\"");
		}
	}
	
}
