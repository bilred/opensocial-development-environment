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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

public class SimpleConfigurationElementImpl implements IConfigurationElement {

	public Object createExecutableExtension(String propertyName) throws CoreException {
		return null;
	}
	
	public String getAttribute(String name) throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getAttributeAsIs(String name) throws InvalidRegistryObjectException {
		return null;
	}
	
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return null;
	}
	
	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		return null;
	}
	
	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		return null;
	}
	
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return null;
	}
	
	public IExtension getDeclaringExtension() throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getName() throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getNamespace() throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getNamespaceIdentifier() throws InvalidRegistryObjectException {
		return null;
	}
	
	public Object getParent() throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getValue() throws InvalidRegistryObjectException {
		return null;
	}
	
	public String getValueAsIs() throws InvalidRegistryObjectException {
		return null;
	}
	
	public boolean isValid() {
		return false;
	}
}