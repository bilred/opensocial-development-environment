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
package com.googlecode.osde.internal.editors.outline;

import com.googlecode.osde.internal.Activator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class GadgetXmlOutlineLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		ElementModel model = (ElementModel)element;
		if (model.getName().equalsIgnoreCase("moduleprefs")) {
			return Activator.getDefault().getImageRegistry().get("icons/opensocial.gif");
		} else if (model.getName().equalsIgnoreCase("content")) {
			return Activator.getDefault().getImageRegistry().get("icons/page_component.gif");
		} else if (model.getName().equalsIgnoreCase("require")) {
			return Activator.getDefault().getImageRegistry().get("icons/i_require.gif");
		} else if (model.getName().equalsIgnoreCase("optional")) {
			return Activator.getDefault().getImageRegistry().get("icons/i_optional.gif");
		} else if (model.getName().equalsIgnoreCase("param")) {
			return Activator.getDefault().getImageRegistry().get("icons/i_param.gif");
		} else if (model.getName().equalsIgnoreCase("locale")) {
			return Activator.getDefault().getImageRegistry().get("icons/icon_world.gif");
		} else if (model.getName().equalsIgnoreCase("msg")) {
			return Activator.getDefault().getImageRegistry().get("icons/16-em-pencil.gif");
		} else if (model.getName().equalsIgnoreCase("icon")) {
			return Activator.getDefault().getImageRegistry().get("icons/i_icon.gif");
		} else if (model.getName().equalsIgnoreCase("userpref")) {
			return Activator.getDefault().getImageRegistry().get("icons/icon_settings.gif");
		} else if (model.getName().equalsIgnoreCase("enumvalue")) {
			return Activator.getDefault().getImageRegistry().get("icons/i_enumvalue.gif");
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		ElementModel model = (ElementModel)element;
		String name = model.getName();
		if (name.equalsIgnoreCase("require") || name.equalsIgnoreCase("optional")) {
			String feature = model.getAttributes().get("feature");
			return (feature != null ? feature : "");
		}
		if (name.equalsIgnoreCase("content")) {
			String view = model.getAttributes().get("view");
			return (view != null ? view : "");
		}
		if (name.equalsIgnoreCase("moduleprefs")) {
			String title = model.getAttributes().get("title");
			return (title != null ? title : "");
		}
		if (name.equalsIgnoreCase("param")) {
			String pname = model.getAttributes().get("name");
			return (pname != null ? pname : "");
		}
		if (name.equalsIgnoreCase("locale")) {
			String lang = model.getAttributes().get("lang");
			String country = model.getAttributes().get("country");
			if (lang != null && country != null) {
				return lang + "_" + country;
			} else if (lang != null) {
				return lang;
			} else if (country != null) {
				return country;
			} else {
				return "(any)";
			}
		}
		if (name.equalsIgnoreCase("msg")) {
			String pname = model.getAttributes().get("name");
			return (pname != null ? pname : "");
		}
		if (name.equalsIgnoreCase("userpref")) {
			String pname = model.getAttributes().get("name");
			return (pname != null ? pname : "");
		}
		if (name.equalsIgnoreCase("enumvalue")) {
			String value = model.getAttributes().get("value");
			return (value != null ? value : "");
		}
		return name;
	}

}
