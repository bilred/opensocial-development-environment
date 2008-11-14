package com.google.gadgets;

public enum FeatureName {
	
	OPENSOCIAL_0_8("opensocial-0.8"),
	OPENSOCIAL_0_7("opensocial-0.7"),
	PUBSUB("pubsub"),
	VIEWS("views"),
	FLASH("flash"),
	SKINS("skins"),
	DYNAMIC_HEIGHT("window"),
	SET_TITLE("settitle"),
	MINI_MESSAGE("minimessage"),
	TABS("tabs");
	
	private String realName;
	
	private FeatureName(String realName) {
		this.realName = realName;
	}
	
	public String toString() {
		return realName;
	}
	
	public static FeatureName getFeatureName(String realName) {
		for(FeatureName featureName : FeatureName.values()) {
			if (featureName.realName.equals(realName)) {
				return featureName;
			}
		}
		return null;
	}

}
