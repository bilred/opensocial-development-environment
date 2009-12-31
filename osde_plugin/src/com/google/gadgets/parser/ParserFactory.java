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

/**
 * A thread-safe factory for generating parsers.
 *
 * Example usage for gadget XML parser clients:
 * <code>IParser gadgetXMLParser = ParserFactory.createParser(ParserType.GADGET_XML_PARSER);</code>
 *
 * To add a new parser to this factory, make it implement IParser interface and extend
 * Apache Digester. And then add it to the following ParserType enum and modify createParser()
 * accordingly.
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public class ParserFactory {

    // disable instance construction

    private ParserFactory() {
    }

    public static synchronized IParser createParser(ParserType type) {
        switch (type) {
            case GADGET_XML_PARSER:
                return new GadgetXMLParser();
            case MESSAGE_BUNDLE_XML_PARSER:
                return new MessageBundleXMLParser();
            default:
                return null;
        }
    }
}
