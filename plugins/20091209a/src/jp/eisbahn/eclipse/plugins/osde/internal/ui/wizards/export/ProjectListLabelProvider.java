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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.export;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class ProjectListLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		switch(columnIndex) {
		case 0:
			ImageDescriptor descriptor = Activator.getDefault().getImageRegistry().getDescriptor("icons/project.gif");
			return descriptor.createImage();
		default:
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		IProject project = (IProject)element;
		switch(columnIndex) {
		case 1:
			return project.getName();
		default:
			return null;
		}
	}
	
}