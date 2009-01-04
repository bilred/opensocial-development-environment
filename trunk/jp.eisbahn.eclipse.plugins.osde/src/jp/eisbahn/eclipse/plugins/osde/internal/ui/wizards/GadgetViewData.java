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

import java.io.Serializable;

import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;

@SuppressWarnings("serial")
public class GadgetViewData implements Serializable {

	private ViewName viewName;
	
	private ViewType type;
	
	private boolean createExternalJavaScript;
	
	private boolean createInitFunction;
	
	private String href;

	public ViewName getViewName() {
		return viewName;
	}

	public void setViewName(ViewName viewName) {
		this.viewName = viewName;
	}

	public ViewType getType() {
		return type;
	}

	public void setType(ViewType type) {
		this.type = type;
	}

	public boolean isCreateExternalJavaScript() {
		return createExternalJavaScript;
	}

	public void setCreateExternalJavaScript(boolean createExternalJavaScript) {
		this.createExternalJavaScript = createExternalJavaScript;
	}

	public boolean isCreateInitFunction() {
		return createInitFunction;
	}

	public void setCreateInitFunction(boolean createInitFunction) {
		this.createInitFunction = createInitFunction;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
}
