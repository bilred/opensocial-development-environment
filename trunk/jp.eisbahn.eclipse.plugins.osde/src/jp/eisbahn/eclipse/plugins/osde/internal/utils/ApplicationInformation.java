package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import com.google.gadgets.Module;

public class ApplicationInformation {

	private String appId;
	private Module module;

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setModule(Module module) {
		this.module = module;
	}
	
	public String getAppId() {
		return appId;
	}
	
	public Module getModule() {
		return module;
	}

}
