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
package org.apache.shindig.social.opensocial.hibernate.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

@Entity
@Table(name="activity")
public class ActivityImpl implements Activity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="app_id", length=255)
	protected String appId;
	
	@Basic
	@Column(name="body", length=255)
	protected String body;

	@Basic
	@Column(name="body_id", length=255)
	protected String bodyId;
	
	@Basic
	@Column(name="external_id", length=255)
	protected String externalId;
	
	@Basic
	@Column(name="activity_id", length=255)
	protected String id;
	
	@Basic
	@Column(name="updated")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;
	
	@ManyToMany(targetEntity=MediaItemImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="activity_media",
			joinColumns=@JoinColumn(name="activity_id", referencedColumnName="oid"),
			inverseJoinColumns=@JoinColumn(name="media_id", referencedColumnName="oid"))
	protected List<MediaItem> mediaItems;
	
	@Basic
	@Column(name="posted_time")
	protected Long postedTime;
	
	@Basic
	@Column(name="priority")
	protected Float priority;
	
	@Basic
	@Column(name="stream_favicon_url", length=255)
	protected String streamFaviconUrl;
	
	@Basic
	@Column(name="stream_source_url", length=255)
	protected String streamSourceUrl;
	
	@Basic
	@Column(name="stream_title", length=255)
	protected String streamTitle;
	
	@Basic
	@Column(name="stream_url", length=255)
	protected String streamUrl;
	
	@CollectionOfElements
	@JoinTable(name="template_param", joinColumns=@JoinColumn(name="activity_id"))
	@MapKey(columns=@Column(name="template_param_name"))
	@Column(name="template_param_value", nullable=false)
	protected Map<String, String> templateParams;
	
	@Basic
	@Column(name="title", length=255)
	protected String title;
	
	@Basic
	@Column(name="title_id", length=255)
	protected String titleId;
	
	@Basic
	@Column(name="url", length=255)
	protected String url;
	
	@Basic
	@Column(name="user_id", length=255)
	protected String userId;
	
	public ActivityImpl() {
		super();
		mediaItems = new ArrayList<MediaItem>();
		templateParams = new HashMap<String, String>();
	}
	
// --- getters

	public long getObjectId() {
		return objectId;
	}

	public String getAppId() {
		return appId;
	}

	public String getBody() {
		return body;
	}

	public String getBodyId() {
		return bodyId;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getId() {
		return id;
	}

	public List<MediaItem> getMediaItems() {
		return mediaItems;
	}

	public Long getPostedTime() {
		return postedTime;
	}

	public Float getPriority() {
		return priority;
	}

	public String getStreamFaviconUrl() {
		return streamFaviconUrl;
	}

	public String getStreamSourceUrl() {
		return streamSourceUrl;
	}

	public String getStreamTitle() {
		return streamTitle;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public Map<String, String> getTemplateParams() {
		return templateParams;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleId() {
		return titleId;
	}

	public Date getUpdated() {
		return updated;
	}

	public String getUrl() {
		return url;
	}

	public String getUserId() {
		return userId;
	}
	
// --- setters

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMediaItems(List<MediaItem> mediaItems) {
		this.mediaItems = mediaItems;
	}

	public void setPostedTime(Long postedTime) {
		this.postedTime = postedTime;
	}

	public void setPriority(Float priority) {
		this.priority = priority;
	}

	public void setStreamFaviconUrl(String streamFaviconUrl) {
		this.streamFaviconUrl = streamFaviconUrl;
	}

	public void setStreamSourceUrl(String streamSourceUrl) {
		this.streamSourceUrl = streamSourceUrl;
	}

	public void setStreamTitle(String streamTitle) {
		this.streamTitle = streamTitle;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public void setTemplateParams(Map<String, String> templateParams) {
		this.templateParams = templateParams;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
