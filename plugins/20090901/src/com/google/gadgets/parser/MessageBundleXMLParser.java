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
import java.io.File;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;

import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.xml.sax.SAXException;

import com.google.gadgets.MessageBundle;

/**
 * Parser class for parsing message bundle XML file using Apache's Digester
 * Refer to http://code.google.com/intl/zh-TW/apis/gadgets/docs/reference.html
 * for complete information of gadgets XML specifications
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class MessageBundleXMLParser extends Digester implements IParser {
	
	public MessageBundleXMLParser() {
		super();
		initialize();
	}
	
	protected void initialize() {
		addRule("messagebundle", new ObjectCreateRule(MessageBundle.class));
		addRule("messagebundle/msg", new ObjectCreateRule(MessageBundle.Msg.class));
		String[] propertyNames = new String[]{"name"};
		String[] attributeNames = new String[]{"name"};
		addRule("messagebundle/msg", new SetPropertiesRule(propertyNames, attributeNames));
		addRule("messagebundle/msg", new CallMethodRule("setContent", 0));
		addRule("messagebundle/msg", new SetNextRule("addMessage"));
	}
	
	@Override
	public MessageBundle parse(File inputFile) {
		try {
			MessageBundle msgBundle = (MessageBundle)super.parse(inputFile);
			return msgBundle;
		} catch (SAXException saxe) {
			Logging.error("Error parsing message bundle files:");
			Logging.error(saxe.toString());
		} catch (IOException ioe) {
			Logging.error("Error loading message bundle files:");
			Logging.error(ioe.toString());
		} finally {
			clear();
		}
		return null;
	}
}