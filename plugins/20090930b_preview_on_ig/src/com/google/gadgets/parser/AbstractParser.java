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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * A new parser in OSDE must extend this abstract class and provide implementation
 * for initialize() method to define behaviors of the digester. The new parser should also
 * register itself in ParserType enum. Refer to GadgetXMLParser.java as an example. 
 * 
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public abstract class AbstractParser implements IParser {
	
	protected Digester digester;
	
	protected AbstractParser() {
		digester = new Digester();
		initialize();
	}
	
	protected abstract void initialize();

	public Object parse(File file) {
		try {
			return parse(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object parse(InputStream stream) {
		try {
			return digester.parse(stream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			digester.clear();
		}
		return null;
	}

}
