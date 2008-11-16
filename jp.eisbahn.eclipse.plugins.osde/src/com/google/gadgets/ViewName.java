package com.google.gadgets;

public enum ViewName {

	canvas("Canvas"),
	profile("Profile"),
	preview("Preview"),
	home("Home");
	
	private String displayName;

	private ViewName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static ViewName getViewName(String displayName) {
		for(ViewName viewName : ViewName.values()) {
			if (viewName.displayName.equals(displayName)) {
				return viewName;
			}
		}
		return null;
	}
	
}
