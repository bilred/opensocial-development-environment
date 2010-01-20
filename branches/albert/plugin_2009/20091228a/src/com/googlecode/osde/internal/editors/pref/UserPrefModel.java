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
package com.googlecode.osde.internal.editors.pref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gadgets.model.Module.UserPref;
import com.google.gadgets.model.Module.UserPref.EnumValue;

public class UserPrefModel {

	public enum DataType {
		STRING("string"),
		BOOL("bool"),
		ENUM("enum"),
		HIDDEN("hidden"),
		LIST("list");
		
		private String displayName;

		private DataType(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public static DataType getDataType(String name) {
			return DataType.valueOf(name.toUpperCase());
		}
	}
	
	private String name;
	private String displayName;
	private String defaultValue;
	private boolean required;
	private DataType dataType;
	private Map<String, String> enumValueMap;
	
	public UserPrefModel() {
		super();
		enumValueMap = new HashMap<String, String>();
	}
	
	public UserPrefModel(UserPref raw) {
		this();
		name = raw.getName();
		displayName = raw.getDisplayName();
		defaultValue = raw.getDefaultValue();
		required = Boolean.parseBoolean(raw.getRequired());
		dataType = DataType.getDataType(raw.getDatatype());
		List<EnumValue> rawEnumValueList = raw.getEnumValue();
		for (EnumValue enumValue : rawEnumValueList) {
			enumValueMap.put(enumValue.getValue(), enumValue.getDisplayValue());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	public Map<String, String> getEnumValueMap() {
		return enumValueMap;
	}
	
	public void setEnumValueMap(Map<String, String> enumValueMap) {
		this.enumValueMap = enumValueMap;
	}
	
}
