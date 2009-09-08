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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.apache.commons.lang.StringUtils;

import com.google.gadgets.model.MessageBundle;
import com.google.gadgets.model.Module;
import com.google.gadgets.model.MessageBundle.Msg;
import com.google.gadgets.model.Module.Content;
import com.google.gadgets.model.Module.ModulePrefs;
import com.google.gadgets.model.Module.UserPref;
import com.google.gadgets.model.Module.ModulePrefs.Icon;
import com.google.gadgets.model.Module.ModulePrefs.Locale;
import com.google.gadgets.model.Module.ModulePrefs.Optional;
import com.google.gadgets.model.Module.ModulePrefs.Require;
import com.google.gadgets.model.Param;
import com.google.gadgets.model.Module.UserPref.EnumValue;

/**
 * Writes gadget contents into files
 * 
 * @author Yoichiro Tanaka
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class GadgetXmlSerializer {

	public static String serialize(Module module) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<Module>\n");
		sb.append("  <ModulePrefs");
		sb.append(createModulePrefsAttributes(module));
		sb.append(createRequiresAndOptionals(module));
		sb.append(createLocales(module));
		sb.append(createIcon(module));
		sb.append("  </ModulePrefs>\n");
		sb.append(createUserPrefs(module));
		sb.append(createContents(module));
		sb.append("</Module>");
		return sb.toString();
	}
	
	private static String createIcon(Module module) {
		StringBuilder sb = new StringBuilder();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<?> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (Object element : elements) {
			Object value = element;
			if (value instanceof Icon) {
				Icon icon = (Icon)value;
				sb.append("        <Icon");
				createAttribute("mode", icon.getMode(), sb);
				createAttribute("type", icon.getType(), sb);
				sb.append(">");
				sb.append(Gadgets.trim(icon.getValue()));
				sb.append("</Icon>\n");
			}
		}
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

	private static String createLocales(Module module) {
		StringBuilder strBuilder = new StringBuilder();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<?> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (Object element : elements) {
			if (element instanceof Locale) {
				Locale locale = (Locale) element;
				if (!locale.isInlined()) {
					strBuilder.append("    <Locale");
					createAttribute("lang", locale.getLang(), strBuilder);
					createAttribute("country", locale.getCountry(), strBuilder);
					createAttribute("messages", locale.getMessages(), strBuilder);
					createAttribute("language_direction", locale.getLanguageDirection(), strBuilder);
					strBuilder.append(" />\n");
				} else {
					strBuilder.append("    <Locale");
					createAttribute("lang", locale.getLang(), strBuilder);
					createAttribute("country", locale.getCountry(), strBuilder);
					createAttribute("language_direction", locale.getLanguageDirection(), strBuilder);
					strBuilder.append(">\n");
					for (Msg msg : locale.getInlineMessages()) {
						strBuilder.append(msg.toString());
						strBuilder.append("\n");
					}
					strBuilder.append("    </Locale>\n");
				}
			}
		}
		return strBuilder.toString();
	}
	
	/**
	 * Given a message bundle Java Bean, this method serializes its contents out into a
	 * file consistent with the specification of gadget message bundle XML.
	 * Refer to http://code.google.com/intl/zh-TW/apis/gadgets/docs/i18n.html
	 * for more details.
	 * 
	 * @param msgBundle Message bundle java bean to be written to the file
	 * @param filePath Path of the written file (not including the file name)
	 * @param lang Language of this message bundle
	 * @param country Country of this message bundle
	 * 
	 * @return true if the file is written successfully
	 * 		   false otherwise
	 */
	public static boolean writeMessageBundleFile(MessageBundle msgBundle, String filePath, String lang, String country) {
		// Construct the full path of the file and checks if it already exists
		// If it does, delete it
		File path = new File(filePath);
		String fileName = path.getAbsolutePath() + File.separator + lang + "_" + country + ".xml";
		File targetFile = new File(fileName);
		if (targetFile.exists()) {
			targetFile.delete();
		}
		
		try {
			FileWriter fout = new FileWriter(fileName);
			fout.write(msgBundle.toString());
			fout.flush();
			fout.close();
		} catch (IOException ioe) {
			Logging.error("Error writing message bundle file: " + fileName);
			Logging.error(ioe.toString());
			return false;
		}
		return true;
	}

	private static String createRequiresAndOptionals(Module module) {
		StringBuilder sb = new StringBuilder();
		ModulePrefs modulePrefs = module.getModulePrefs();
		List<?> elements = modulePrefs.getRequireOrOptionalOrPreload();
		for (Object element : elements) {
			Object value = element;
			if (value instanceof Require) {
				Require require = (Require)value;
				sb.append("        <Require feature=\"" + require.getFeature() + "\"");
				List<Param> params = require.getParam();
				if (params.isEmpty()) {
					sb.append(" />\n");
				} else {
					sb.append(">\n");
					for (Param param : params) {
						sb.append("            <Param name=\"");
						sb.append(param.getName());
						sb.append("\">");
						sb.append(escape(param.getValue()));
						sb.append("</Param>\n");
					}
					sb.append("        </Require>\n");
				}
			} else if (value instanceof Optional) {
				Optional optional = (Optional)value;
				sb.append("        <Optional feature=\"" + optional.getFeature() + "\"");
				List<Param> params = optional.getParam();
				if (params.isEmpty()) {
					sb.append(" />\n");
				} else {
					sb.append(">\n");
					for (Param param : params) {
						sb.append("            <Param name=\"");
						sb.append(param.getName());
						sb.append("\">");
						sb.append(escape(param.getValue()));
						sb.append("</Param>\n");
					}
					sb.append("        </Optional>\n");
				}
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
	
	private static void createAttribute(String name, String value, StringBuilder strBuilder) {
		if (StringUtils.isNotEmpty(value)) {
			strBuilder.append(" " + name + "=\"" + escape(value) + "\"");
		}
	}
	
}
