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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import org.eclipse.core.resources.IProject;

/**
 * Place holder for storing information of launching application
 * with local shindig/database support.
 */
public class LaunchApplicationInformation {

	private String viewer;
	private String owner;
	private String view;
	private String width;
	private String appId;
	private boolean useExternalBrwoser;
	private String country;
	private String language;
	private IProject project;
	private String url;
	private String applicationTitle;

	public LaunchApplicationInformation(String viewer, String owner,
			String view, String width, String appId,
			boolean useExternalBrwoser, String country, String language,
			IProject project, String url, String applicationTitle) {
		this.viewer = viewer;
		this.owner = owner;
		this.view = view;
		this.width = width;
		this.appId = appId;
		this.useExternalBrwoser = useExternalBrwoser;
		this.country = country;
		this.language = language;
		this.project = project;
		this.url = url;
		this.applicationTitle = applicationTitle;
	}

	public String getViewer() {
		return viewer;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean isUseExternalBrwoser() {
		return useExternalBrwoser;
	}

	public void setUseExternalBrwoser(boolean useExternalBrwoser) {
		this.useExternalBrwoser = useExternalBrwoser;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApplicationTitle() {
		return applicationTitle;
	}

	public void setApplicationTitle(String applicationTitle) {
		this.applicationTitle = applicationTitle;
	}
}