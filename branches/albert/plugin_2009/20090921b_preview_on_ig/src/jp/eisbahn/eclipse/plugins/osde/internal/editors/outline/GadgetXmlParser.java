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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.outline;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GadgetXmlParser {

	private class HandlerImpl extends DefaultHandler {
		
		private ElementModel root;
		private Stack<ElementModel> parentStack = new Stack<ElementModel>();
		private Locator locator;

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			ElementModel model = new ElementModel();
			model.setName(name);
			model.setLineNumber(locator.getLineNumber());
			for (int i = 0; i < attributes.getLength(); i++) {
				model.putAttribute(attributes.getQName(i), attributes.getValue(i));
			}
			if (root == null) {
				root = model;
				parentStack.push(model);
			} else {
				ElementModel parent = parentStack.peek();
				parent.addChild(model);
				parentStack.push(model);
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {
			parentStack.pop();
		}

		public ElementModel getResult() {
			return root;
		}
	}

	public ElementModel parse(String source) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		StringReader reader = new StringReader(source);
		HandlerImpl handler = new HandlerImpl();
		parser.parse(new InputSource(reader), handler);
		return handler.getResult();
	}
	
}