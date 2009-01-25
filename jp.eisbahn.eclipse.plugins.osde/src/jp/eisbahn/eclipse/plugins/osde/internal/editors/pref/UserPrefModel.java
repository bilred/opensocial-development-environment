package jp.eisbahn.eclipse.plugins.osde.internal.editors.pref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.gadgets.Module.UserPref;
import com.google.gadgets.Module.UserPref.EnumValue;

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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserPrefModel)) {
			return false;
		} else {
			UserPrefModel target = (UserPrefModel)obj;
			return new EqualsBuilder()
					.append(name, target.getName())
					.isEquals();
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(name)
				.toHashCode();
	}

}
