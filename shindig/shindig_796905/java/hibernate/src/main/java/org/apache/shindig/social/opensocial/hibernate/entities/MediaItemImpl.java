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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;

@Entity
@Table(name="media_item")
public class MediaItemImpl implements MediaItem {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	private long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@ManyToMany(targetEntity=ActivityImpl.class, mappedBy="mediaItems", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Activity> activities;
	
	@Basic
	@Column(name="mime_type", length=255)
	protected String mimeType;
	
	@Enumerated(EnumType.STRING)
	private Type type;
	
	@Basic
	@Column(name="url", length=255)
	protected String url;

	@Basic
	@Column(name="thumbnail_url", length=255)
	protected String thumbnailUrl;

	public MediaItemImpl() {
		super();
		activities = new ArrayList<Activity>();
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public Type getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getObjectId() {
		return objectId;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

}
