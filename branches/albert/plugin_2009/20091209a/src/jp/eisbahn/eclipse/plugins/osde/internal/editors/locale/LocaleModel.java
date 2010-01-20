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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gadgets.model.Module.ModulePrefs.Locale;
import com.google.gadgets.model.MessageBundle.Msg;

public class LocaleModel implements Comparable<LocaleModel> {

    private static final Logger logger = new Logger(LocaleModel.class);

	public static final String MESSAGE_BUNDLE_FILENAME_PREFIX = "messages_";
	
	private String country;
	private String lang;
	private boolean internal;
	private Map<String, String> messages;
	
	public LocaleModel() {
		super();
		internal = true;
		messages = new TreeMap<String, String>();
	}
	
	public LocaleModel(Locale rawModel, IProject project) {
		this();
		country = rawModel.getCountry();
		lang = rawModel.getLang();
		internal = StringUtils.isEmpty(rawModel.getMessages());
		if (internal) {
			List<Msg> msgs = rawModel.getInlineMessages();
			for (Msg msg : msgs) {
				messages.put(msg.getName(), msg.getContent());
			}
		} else {
			loadMessageBundleFile(project);
		}
	}
	
	private void loadMessageBundleFile(IProject project) {
		String fileName = MESSAGE_BUNDLE_FILENAME_PREFIX + lang + "_" + country + ".xml";
		IFile file = project.getFile(fileName);
		if (file.exists()) {
			try {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(file.getContents());
				NodeList msgList = document.getElementsByTagName("msg");
				for (int i = 0; i < msgList.getLength(); i++) {
					Element msg = (Element)msgList.item(i);
					String name = msg.getAttribute("name");
					String value = msg.getTextContent();
					messages.put(name, value);
				}
			} catch (ParserConfigurationException e) {
				logger.warn("Loading message bundle file failed.", e);
			} catch (SAXException e) {
				logger.warn("Loading message bundle file failed.", e);
			} catch (IOException e) {
				logger.warn("Loading message bundle file failed.", e);
			} catch (CoreException e) {
				logger.warn("Loading message bundle file failed.", e);
			}
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof LocaleModel)) {
//			return false;
//		} else {
//			LocaleModel target = (LocaleModel)obj;
//			return new EqualsBuilder()
//					.append(country, target.getCountry())
//					.append(lang, target.getLang())
//					.isEquals();
//		}
//	}
//
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder(17, 37)
//				.append(country)
//				.append(lang)
//				.toHashCode();
//	}

	public int compareTo(LocaleModel o) {
		return new CompareToBuilder()
				.append(country, o.getCountry())
				.append(lang, o.getLang())
				.toComparison();
	}

}
