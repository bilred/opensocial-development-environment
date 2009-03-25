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
package com.google.gadgets;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Module {
	
	protected Module.ModulePrefs modulePrefs;
	protected List<Module.UserPref> userPref;
	protected List<Module.Content> content;
	
	public Module.ModulePrefs getModulePrefs() {
		return modulePrefs;
	}
	
	public void setModulePrefs(Module.ModulePrefs value) {
		this.modulePrefs = value;
	}
	
	public List<Module.UserPref> getUserPref() {
		if (userPref == null) {
			userPref = new ArrayList<Module.UserPref>();
		}
		return this.userPref;
	}
	
	public List<Module.Content> getContent() {
		if (content == null) {
			content = new ArrayList<Module.Content>();
		}
		return this.content;
	}
	
	public static class Content {
		
		protected String value;
		protected String type;
		protected String href;
		protected String view;
		protected BigInteger preferredHeight;
		protected BigInteger preferredWidth;
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public String getType() {
			if (type == null) {
				return "html";
			} else {
				return type;
			}
		}
		
		public void setType(String value) {
			this.type = value;
		}
		
		public String getHref() {
			return href;
		}
		
		public void setHref(String value) {
			this.href = value;
		}
		
		public String getView() {
			return view;
		}
		
		public void setView(String value) {
			this.view = value;
		}
		
		public BigInteger getPreferredHeight() {
			return preferredHeight;
		}
		
		public void setPreferredHeight(BigInteger value) {
			this.preferredHeight = value;
		}
		
		public BigInteger getPreferredWidth() {
			return preferredWidth;
		}
		
		public void setPreferredWidth(BigInteger value) {
			this.preferredWidth = value;
		}
		
	}
	
	public static class ModulePrefs {
		
		protected List<Object> requireOrOptionalOrPreload;
		protected String title;
		protected String titleUrl;
		protected String description;
		protected String author;
		protected String authorEmail;
		protected String screenshot;
		protected String thumbnail;
		
		public List<Object> getRequireOrOptionalOrPreload() {
			if (requireOrOptionalOrPreload == null) {
				requireOrOptionalOrPreload = new ArrayList<Object>();
			}
			return this.requireOrOptionalOrPreload;
		}
		
		public void addRequireOrOptionalOrPreload(Object obj) {
			if (requireOrOptionalOrPreload == null) {
				requireOrOptionalOrPreload = new ArrayList<Object>();
			}
			requireOrOptionalOrPreload.add(obj);
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTitleUrl() {
			return titleUrl;
		}

		public void setTitleUrl(String titleUrl) {
			this.titleUrl = titleUrl;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getAuthorEmail() {
			return authorEmail;
		}

		public void setAuthorEmail(String authorEmail) {
			this.authorEmail = authorEmail;
		}

		public String getScreenshot() {
			return screenshot;
		}

		public void setScreenshot(String screenshot) {
			this.screenshot = screenshot;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
		
		public static class Require {
			
			protected List<Param> param;
			protected String feature;
			
			public List<Param> getParam() {
				if (param == null) {
					param = new ArrayList<Param>();
				}
				return this.param;
			}
			
			public void addParam(Param value) {
				if (param == null) {
					param = new ArrayList<Param>();
				}
				param.add(value);
			}

			public String getFeature() {
				return feature;
			}

			public void setFeature(String feature) {
				this.feature = feature;
			}
			
		}

		public static class Optional {
			
			protected List<Param> param;
			protected String feature;
			
			public List<Param> getParam() {
				if (param == null) {
					param = new ArrayList<Param>();
				}
				return this.param;
			}

			public void addParam(Param value) {
				if (param == null) {
					param = new ArrayList<Param>();
				}
				param.add(value);
			}

			public String getFeature() {
				return feature;
			}

			public void setFeature(String feature) {
				this.feature = feature;
			}
			
		}

		public static class Icon {
			
			protected String value;
			protected String mode;
			protected String type;
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public String getMode() {
				return mode;
			}
			public void setMode(String mode) {
				this.mode = mode;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			
		}
		
		public static class Link {
			
			protected String href;
			protected String rel;
			protected String method;
			
			public String getHref() {
				return href;
			}
			
			public void setHref(String href) {
				this.href = href;
			}
			
			public String getRel() {
				return rel;
			}
			
			public void setRel(String rel) {
				this.rel = rel;
			}

			public String getMethod() {
				return method;
			}

			public void setMethod(String method) {
				this.method = method;
			}
			
		}
		
		public static class Locale {
			
			protected List<Module.ModulePrefs.Locale.Msg> msg;
			protected String lang;
			protected String country;
			protected String messages;
			protected String languageDirection;
			
			public List<Module.ModulePrefs.Locale.Msg> getMsg() {
				if (msg == null) {
					msg = new ArrayList<Module.ModulePrefs.Locale.Msg>();
				}
				return this.msg;
			}
			
			public void addMsg(Module.ModulePrefs.Locale.Msg value) {
				if (msg == null) {
					msg = new ArrayList<Module.ModulePrefs.Locale.Msg>();
				}
				msg.add(value);
			}

			public String getLang() {
				return lang;
			}

			public void setLang(String lang) {
				this.lang = lang;
			}

			public String getCountry() {
				return country;
			}

			public void setCountry(String country) {
				this.country = country;
			}

			public String getMessages() {
				return messages;
			}

			public void setMessages(String messages) {
				this.messages = messages;
			}

			public String getLanguageDirection() {
				return languageDirection;
			}

			public void setLanguageDirection(String languageDirection) {
				this.languageDirection = languageDirection;
			}
			
			public static class Msg {
				
				protected String value;
				protected String name;
				protected String desc;
				
				public String getValue() {
					return value;
				}
				
				public void setValue(String value) {
					this.value = value;
				}
				
				public String getName() {
					return name;
				}
				
				public void setName(String name) {
					this.name = name;
				}
				
				public String getDesc() {
					return desc;
				}
				
				public void setDesc(String desc) {
					this.desc = desc;
				}
				
			}
			
		}
		
		public static class OAuth {
			
			protected List<Module.ModulePrefs.OAuth.Service> service;
			
			public List<Module.ModulePrefs.OAuth.Service> getService() {
				if (service == null) {
					service = new ArrayList<Module.ModulePrefs.OAuth.Service>();
				}
				return this.service;
			}
			
			public void addService(Module.ModulePrefs.OAuth.Service value) {
				if (service == null) {
					service = new ArrayList<Module.ModulePrefs.OAuth.Service>();
				}
				service.add(value);
			}

			public static class Service {
				
				protected String name;
				protected Module.ModulePrefs.OAuth.Service.Request request;
				protected Module.ModulePrefs.OAuth.Service.Access access;
				protected Module.ModulePrefs.OAuth.Service.Authorization authorization;
				
				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}
				
				public Module.ModulePrefs.OAuth.Service.Request getRequest() {
					return request;
				}
				
				public void setRequest(Module.ModulePrefs.OAuth.Service.Request request) {
					this.request = request;
				}
				
				public Module.ModulePrefs.OAuth.Service.Access getAccess() {
					return access;
				}
				
				public void setAccess(Module.ModulePrefs.OAuth.Service.Access access) {
					this.access = access;
				}
				
				public Module.ModulePrefs.OAuth.Service.Authorization getAuthorization() {
					return authorization;
				}
				
				public void setAuthorization(
						Module.ModulePrefs.OAuth.Service.Authorization authorization) {
					this.authorization = authorization;
				}
				
				public static class Request {
					
					protected String url;
					protected String method;
					protected String paramLocation;
					
					public String getUrl() {
						return url;
					}
					
					public void setUrl(String url) {
						this.url = url;
					}
					
					public String getMethod() {
						if (method == null) {
							return "GET";
						} else {
							return method;
						}
					}
					
					public void setMethod(String method) {
						this.method = method;
					}
					
					public String getParamLocation() {
						if (paramLocation == null) {
							return "auth-header";
						} else {
							return paramLocation;
						}
					}
					
					public void setParamLocation(String paramLocation) {
						this.paramLocation = paramLocation;
					}

				}

				public static class Access {
					
					protected String url;
					protected String method;
					protected String paramLocation;
					
					public String getUrl() {
						return url;
					}
					
					public void setUrl(String url) {
						this.url = url;
					}
					
					public String getMethod() {
						if (method == null) {
							return "GET";
						} else {
							return method;
						}
					}
					
					public void setMethod(String method) {
						this.method = method;
					}
					
					public String getParamLocation() {
						if (paramLocation == null) {
							return "auth-header";
						} else {
							return paramLocation;
						}
					}
					
					public void setParamLocation(String paramLocation) {
						this.paramLocation = paramLocation;
					}

				}

				public static class Authorization {
					
					protected String url;

					public String getUrl() {
						return url;
					}

					public void setUrl(String url) {
						this.url = url;
					}
					
				}
				
			}
			
		}
		
		public static class Preload {
			
			protected String href;
			protected String authz;
			protected Boolean signOwner;
			protected Boolean signViewer;
			protected String views;
			protected String oauthServiceName;
			protected String oauthTokenName;
			protected String oauthRequestToken;
			protected String oauthRequestTokenSecret;
			
			public String getHref() {
				return href;
			}
			
			public void setHref(String href) {
				this.href = href;
			}
			
			public String getAuthz() {
				if (authz == null) {
					return "none";
				} else {
					return authz;
				}
			}
			
			public void setAuthz(String authz) {
				this.authz = authz;
			}
			
			public Boolean isSignOwner() {
				if (signOwner == null) {
					return true;
				} else {
					return signOwner;
				}
			}
			
			public void setSignOwner(Boolean signOwner) {
				this.signOwner = signOwner;
			}
			
			public Boolean isSignViewer() {
				if (signViewer == null) {
					return true;
				} else {
					return signViewer;
				}
			}
			
			public void setSignViewer(Boolean signViewer) {
				this.signViewer = signViewer;
			}
			
			public String getViews() {
				return views;
			}
			
			public void setViews(String views) {
				this.views = views;
			}
			
			public String getOauthServiceName() {
				return oauthServiceName;
			}
			
			public void setOauthServiceName(String oauthServiceName) {
				this.oauthServiceName = oauthServiceName;
			}
			
			public String getOauthTokenName() {
				return oauthTokenName;
			}
			
			public void setOauthTokenName(String oauthTokenName) {
				this.oauthTokenName = oauthTokenName;
			}
			
			public String getOauthRequestToken() {
				return oauthRequestToken;
			}
			
			public void setOauthRequestToken(String oauthRequestToken) {
				this.oauthRequestToken = oauthRequestToken;
			}
			
			public String getOauthRequestTokenSecret() {
				return oauthRequestTokenSecret;
			}
			
			public void setOauthRequestTokenSecret(String oauthRequestTokenSecret) {
				this.oauthRequestTokenSecret = oauthRequestTokenSecret;
			}
			
		}
		
	}
	
	public static class UserPref {
		
		protected List<Module.UserPref.EnumValue> enumValue;
		protected String name;
		protected String displayName;
		protected String defaultValue;
		protected String required;
		protected String datatype;
		
		public List<Module.UserPref.EnumValue> getEnumValue() {
			if (enumValue == null) {
				enumValue = new ArrayList<Module.UserPref.EnumValue>();
			}
			return this.enumValue;
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

		public String getRequired() {
			return required;
		}

		public void setRequired(String required) {
			this.required = required;
		}

		public String getDatatype() {
			if (datatype == null) {
				return "string";
			} else {
				return datatype;
			}
		}

		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}
		
		public static class EnumValue {
			
			protected String value;
			protected String displayValue;
			
			public String getValue() {
				return value;
			}
			
			public void setValue(String value) {
				this.value = value;
			}
			
			public String getDisplayValue() {
				return displayValue;
			}
			
			public void setDisplayValue(String displayValue) {
				this.displayValue = displayValue;
			}
			
		}
		
	}

}
