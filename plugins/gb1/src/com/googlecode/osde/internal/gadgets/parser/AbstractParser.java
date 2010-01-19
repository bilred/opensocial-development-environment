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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A new parser in OSDE must extend this abstract class and provide implementation
 * for initialize() method to define behaviors of the digester. The new parser should also
 * register itself in ParserType enum. Refer to GadgetXMLParser.java as an example.
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public abstract class AbstractParser implements IParser {

    private Digester digester;

    protected AbstractParser() {
        digester = new Digester();
        initialize(digester);
    }

    protected abstract void initialize(Digester digester);

    public Object parse(File file) throws ParserException {
        try {
            return parse(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ParserException(file.getAbsolutePath() + " not found", e);
        }
    }

    public Object parse(InputStream stream) throws ParserException {
        try {
            Object result = digester.parse(stream);
            if (result == null) {
                throw new ParserException("Digester returned null as the result of parsing.");
            }
            return result;
        } catch (IOException e) {
            throw new ParserException("IO error during parsing.", e);
        } catch (SAXException e) {
            throw new ParserException("Parsing error", e);
        } finally {
            digester.clear();
        }
    }

    /**
     * An object factory that:
     * <ul>
     * <li>Creates an instance of <code>beanClass</code> using its default constructor.
     * <li>Sets any attribute value if its name is either in camel-case or underscore style.
     * <li>Sets any unrecognized attribute name-value pair if the <code>beanClass</code>
     * implements {@link com.googlecode.osde.internal.gadgets.parser.AcceptExtraProperties}.
     * </ul>
     * This object factory simplifies lots of rule configurations. For example, <pre>
     * d.addRule("/a", new ObjectCreateRule(A.class);
     * d.addRule("/a", ["p1", "p2", "p3"], ["p1", "p2", "p3"]); </pre> can be replaced
     * with: <pre>
     * d.addFactoryCreate("/a", new ObjectFactory(A.class);</pre>
     */
    protected static final class ObjectFactory extends AbstractObjectCreationFactory {

        private final Class<?> beanClass;

        public ObjectFactory(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

        @Override
        public Object createObject(Attributes attributes) throws Exception {
            final Object bean = beanClass.newInstance();

            final Map<String, String> unrecognized = new LinkedHashMap<String, String>();
            final Map<String, String> recognized = new HashMap<String, String>();
            final int count = attributes.getLength();

            for (int i = 0; i < count; i++) {
                String propertyName = toCamelCase(attributes.getLocalName(i));
                String value = attributes.getValue(i);

                if (PropertyUtils.isWriteable(bean, propertyName)) {
                    recognized.put(propertyName, value);
                } else {
                    unrecognized.put(attributes.getQName(i), value);
                }
            }

            BeanUtils.populate(bean, recognized);
            if (bean instanceof AcceptExtraProperties) {
                ((AcceptExtraProperties) bean).getExtraProperties().putAll(unrecognized);
            }

            return bean;
        }

        /**
         * Converts underscore style to camel-case style. For example,
         * "some_useful_variable" is converted to "someUsefulVariable".
         */
        static String toCamelCase(String name) {
            if (name == null) {
                return null;
            }

            String[] ps = name.split("_");
            if (ps == null || ps.length == 0) {
                return name;
            }

            StringBuilder b = new StringBuilder();
            for (String p : ps) {
                if (b.length() > 0) {
                    b.append(capitalize(p));
                } else {
                    b.append(p);
                }
            }

            return b.toString();
        }

        private static String capitalize(String name) {
            if (name.length() > 0) {
                return Character.toUpperCase(name.charAt(0)) + name.substring(1);
            } else {
                return "";
            }
        }
    }
}
