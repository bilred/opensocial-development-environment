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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.model.MediaItem.Type;
import org.hibernate.Transaction;
import org.junit.Test;

public class ActivityImplTest extends AbstractEntityTest {
	
	@Test
	public void testTemplateParams() throws Exception {
		Transaction tx = session.beginTransaction();
		ActivityImpl activity = createActivity();
		Map<String, String> templateParams = activity.getTemplateParams();
		templateParams.put("name1", "value1");
		templateParams.put("name2", "value2");
		session.save(activity);
		long oid = activity.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		templateParams = activity.getTemplateParams();
		assertEquals("name1", "value1", templateParams.get("name1"));
		assertEquals("name2", "value2", templateParams.get("name2"));
		tx.commit();
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		templateParams = activity.getTemplateParams();
		templateParams.remove("name1");
		templateParams.put("name2", "value4");
		templateParams.put("name3", "value3");
		session.update(activity);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		templateParams = activity.getTemplateParams();
		assertEquals("name2", "value4", templateParams.get("name2"));
		assertEquals("name3", "value3", templateParams.get("name3"));
		tx.commit();
	}
	
	@Test
	public void testMediaItems() throws Exception {
		Transaction tx = session.beginTransaction();
		ActivityImpl activity = createActivity();
		List<MediaItem> mediaItems = activity.getMediaItems();
		mediaItems.add(createMediaItem("mimeType1", Type.AUDIO, "url1"));
		mediaItems.add(createMediaItem("mimeType2", Type.IMAGE, "url2"));
		session.save(activity);
		long oid = activity.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		mediaItems = activity.getMediaItems();
		assertEquals("mediaItems.size", 2, mediaItems.size());
		assertMediaItem("mimeType1", Type.AUDIO, "url1", (MediaItemImpl)mediaItems.get(0), activity);
		assertMediaItem("mimeType2", Type.IMAGE, "url2", (MediaItemImpl)mediaItems.get(1), activity);
		tx.commit();
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		mediaItems = activity.getMediaItems();
		MediaItemImpl mediaItem = (MediaItemImpl)mediaItems.get(0);
		mediaItem.setMimeType("mimeType3");
		mediaItem.setType(Type.VIDEO);
		mediaItem.setUrl("url3");
		mediaItems.remove(1);
		mediaItems.add(createMediaItem("mimeType4", Type.AUDIO, "url4"));
		session.update(activity);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		mediaItems = activity.getMediaItems();
		assertMediaItem("mimeType3", Type.VIDEO, "url3", (MediaItemImpl)mediaItems.get(0), activity);
		assertMediaItem("mimeType4", Type.AUDIO, "url4", (MediaItemImpl)mediaItems.get(1), activity);
		tx.commit();
	}
	
	private void assertMediaItem(String mimeType, Type type, String url, MediaItemImpl mediaItem, ActivityImpl activity) {
		assertEquals("mimeType", mimeType, mediaItem.getMimeType());
		assertEquals("type", type, mediaItem.getType());
		assertEquals("url", url, mediaItem.getUrl());
		ActivityImpl actual = (ActivityImpl)mediaItem.getActivities().get(0);
		assertEquals("activity", activity, actual);
	}

	private MediaItemImpl createMediaItem(String mimeType, Type type, String url) {
		MediaItemImpl mediaItem = new MediaItemImpl();
		mediaItem.setMimeType(mimeType);
		mediaItem.setType(type);
		mediaItem.setUrl(url);
		return mediaItem;
	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		ActivityImpl activity = createActivity();
		Date updated = activity.getUpdated();
		session.save(activity);
		tx.commit();
		long oid = activity.getObjectId();
		session.clear();
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		tx.commit();
		assertEquals("appId", "appId1", activity.getAppId());
		assertEquals("body", "body1", activity.getBody());
		assertEquals("bodyId", "bodyId1", activity.getBodyId());
		assertEquals("externalId", "externalId1", activity.getExternalId());
		assertEquals("id", "id1", activity.getId());
		assertEquals("postedTime", new Long(12345L), activity.getPostedTime());
		assertEquals("priority", new Float(123F), activity.getPriority());
		assertEquals("streamFaviconUrl", "streamFaviconUrl1", activity.getStreamFaviconUrl());
		assertEquals("streamSourceUrl", "streamSourceUrl1", activity.getStreamSourceUrl());
		assertEquals("streamTitle", "streamTitle1", activity.getStreamTitle());
		assertEquals("streamUrl", "streamUrl1", activity.getStreamUrl());
		assertEquals("title", "title1", activity.getTitle());
		assertEquals("titleId", "titleId1", activity.getTitleId());
		assertEquals("updated", updated, activity.getUpdated());
		assertEquals("url", "url1", activity.getUrl());
		assertEquals("userId", "userId1", activity.getUserId());
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		activity.setAppId("appId2");
		activity.setBody("body2");
		activity.setBodyId("bodyId2");
		activity.setExternalId("externalId2");
		activity.setId("id2");
		activity.setPostedTime(6789L);
		activity.setPriority(456F);
		activity.setStreamFaviconUrl("streamFaviconUrl2");
		activity.setStreamSourceUrl("streamSourceUrl2");
		activity.setStreamTitle("streamTitle2");
		activity.setStreamUrl("streamUrl2");
		activity.setTitle("title2");
		activity.setTitleId("titleId2");
		updated = new Date();
		activity.setUpdated(updated);
		activity.setUrl("url2");
		activity.setUserId("userId2");
		session.update(activity);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		tx.commit();
		assertEquals("appId", "appId2", activity.getAppId());
		assertEquals("body", "body2", activity.getBody());
		assertEquals("bodyId", "bodyId2", activity.getBodyId());
		assertEquals("externalId", "externalId2", activity.getExternalId());
		assertEquals("id", "id2", activity.getId());
		assertEquals("postedTime", new Long(6789L), activity.getPostedTime());
		assertEquals("priority", new Float(456F), activity.getPriority());
		assertEquals("streamFaviconUrl", "streamFaviconUrl2", activity.getStreamFaviconUrl());
		assertEquals("streamSourceUrl", "streamSourceUrl2", activity.getStreamSourceUrl());
		assertEquals("streamTitle", "streamTitle2", activity.getStreamTitle());
		assertEquals("streamUrl", "streamUrl2", activity.getStreamUrl());
		assertEquals("title", "title2", activity.getTitle());
		assertEquals("titleId", "titleId2", activity.getTitleId());
		assertEquals("updated", updated, activity.getUpdated());
		assertEquals("url", "url2", activity.getUrl());
		assertEquals("userId", "userId2", activity.getUserId());
		//
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		session.delete(activity);
		tx.commit();
		tx = session.beginTransaction();
		activity = (ActivityImpl)session.get(ActivityImpl.class, oid);
		assertNull(activity);
		tx.commit();
	}

	private ActivityImpl createActivity() {
		ActivityImpl activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setBody("body1");
		activity.setBodyId("bodyId1");
		activity.setExternalId("externalId1");
		activity.setId("id1");
		activity.setPostedTime(12345L);
		activity.setPriority(123F);
		activity.setStreamFaviconUrl("streamFaviconUrl1");
		activity.setStreamSourceUrl("streamSourceUrl1");
		activity.setStreamTitle("streamTitle1");
		activity.setStreamUrl("streamUrl1");
		activity.setTitle("title1");
		activity.setTitleId("titleId1");
		Date updated = new Date();
		activity.setUpdated(updated);
		activity.setUrl("url1");
		activity.setUserId("userId1");
		return activity;
	}
	
}
