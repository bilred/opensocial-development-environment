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
package com.googlecode.osde.internal.editors;

import java.io.IOException;
import java.io.InputStream;

import com.googlecode.osde.internal.gadgets.model.Module;

import com.googlecode.osde.internal.gadgets.parser.Parser;
import com.googlecode.osde.internal.gadgets.parser.ParserException;
import com.googlecode.osde.internal.gadgets.parser.ParserFactory;
import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

/**
 * Content describer for gadget spec XML files.
 */
public class GadgetXmlDescriber implements IContentDescriber {
    private static final Logger logger = new Logger(GadgetXmlDescriber.class);

    public int describe(InputStream contents, IContentDescription description) throws IOException {
        Parser<Module> parser = ParserFactory.gadgetSpecParser();
        try {
            parser.parse(contents);
        } catch (ParserException e) {
            logger.warn(
                    "Parsing failed in gadget xml describer, returning IContentDescriber.INTERMEDIATE",
                    e);
            return IContentDescriber.INDETERMINATE;
        }
        return IContentDescriber.VALID;
    }

    public QualifiedName[] getSupportedOptions() {
        return new QualifiedName[0];
    }

}
