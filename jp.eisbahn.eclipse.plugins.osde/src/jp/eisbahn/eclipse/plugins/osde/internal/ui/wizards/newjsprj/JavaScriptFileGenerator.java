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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newjsprj;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.google.gadgets.ViewName;

public class JavaScriptFileGenerator {

	private IProject project;
	
	private GadgetXmlData gadgetXmlData;
	
	private EnumMap<ViewName, GadgetViewData> gadgetViewData;
	
	public JavaScriptFileGenerator(IProject project, GadgetXmlData gadgetXmlData, EnumMap<ViewName, GadgetViewData> gadgetViewData) {
		super();
		this.project = project;
		this.gadgetXmlData = gadgetXmlData;
		this.gadgetViewData = gadgetViewData;
	}
	
	public List<IFile> generate(IProgressMonitor monitor) throws UnsupportedEncodingException, CoreException {
		try {
			monitor.beginTask("Generate JavaScript files", gadgetViewData.size());
			List<IFile> resultList = new ArrayList<IFile>();
			Set<ViewName> keySet = gadgetViewData.keySet();
			for (ViewName viewName : keySet) {
				GadgetViewData viewData = gadgetViewData.get(viewName);
				if (viewData.isCreateExternalJavaScript()) {
					IFile file = generateJavaScriptFile(monitor, viewName, viewData);
					resultList.add(file);
				}
				monitor.worked(1);
			}
			return resultList;
		} finally {
			monitor.done();
		}
	}
	
	private IFile generateJavaScriptFile(IProgressMonitor monitor, ViewName viewName, GadgetViewData viewData) throws CoreException, UnsupportedEncodingException {
		IFile generateJavaScriptFile = project.getFile(new Path(viewName.toString() + ".js"));
		String content = "/**\n";
		content += " * " + gadgetXmlData.getTitle() + "\n";
		content += " * This JavaScript file is for " + viewName.getDisplayName() + " view.\n";
		content += " */\n\n";
		if (viewData.isCreateInitFunction()) {
			content += "function init() {\n";
			content += "    // TODO: Write the code for initializing.\n";
			content += "}\n\n";
		}
		content += "// TODO: Write the code for " + viewName.getDisplayName() + " view.\n";
		InputStream in = new ByteArrayInputStream(content.getBytes("UTF8"));
		generateJavaScriptFile.create(in, false, monitor);
		return generateJavaScriptFile;
	}

}
