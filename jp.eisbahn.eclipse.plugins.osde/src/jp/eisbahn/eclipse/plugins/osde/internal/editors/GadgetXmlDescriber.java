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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

import com.google.gadgets.Module;

public class GadgetXmlDescriber implements IContentDescriber {

	public int describe(InputStream contents, IContentDescription description) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(Module.class);
			Unmarshaller um = context.createUnmarshaller();
			Module module = (Module)um.unmarshal(contents);
			return IContentDescriber.VALID;
		} catch (JAXBException e) {
			return IContentDescriber.INDETERMINATE;
		}
		
	}

	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

}
