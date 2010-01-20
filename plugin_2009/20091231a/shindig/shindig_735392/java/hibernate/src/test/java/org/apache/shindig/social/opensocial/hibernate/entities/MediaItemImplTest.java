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

import java.util.Date;
import java.util.List;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem.Type;
import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

public class MediaItemImplTest extends AbstractEntityTest {
	
	@Test
	public void testActivities() throws Exception {
		Transaction tx = session.beginTransaction();
		MediaItemImpl mediaItem = createMediaItem();
		List<Activity> activities = mediaItem.getActivities();
		Date updated1 = new Date();
		ActivityImpl activity = createActivity("appId1", "body1", "bodyId1", "externalId1",
				"id1", 123L, 456F, "streamFaviconUrl1", "streamSourceUrl1", "streamTitle1",
				"streamUrl1", "title1", "titleId1", updated1, "url1", "userId1");
		activity.getMediaItems().add(mediaItem);
		activities.add(activity);
		Date updated2 = new Date();
		activity = createActivity("appId2", "body2", "bodyId2", "externalId2",
				"id2", 456L, 789F, "streamFaviconUrl2", "streamSourceUrl2", "streamTitle2",
				"streamUrl2", "title2", "titleId2", updated2, "url2", "userId2");
		activity.getMediaItems().add(mediaItem);
		activities.add(activity);
		session.save(mediaItem);
		long oid = mediaItem.getObjectId();
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		activities = mediaItem.getActivities();
		assertEquals("activities.size", 2, activities.size());
		assertActivity("appId1", "body1", "bodyId1", "externalId1",
				"id1", 123L, 456F, "streamFaviconUrl1", "streamSourceUrl1", "streamTitle1",
				"streamUrl1", "title1", "titleId1", updated1, "url1", "userId1",
				(ActivityImpl)activities.get(0), mediaItem);
		assertActivity("appId2", "body2", "bodyId2", "externalId2",
				"id2", 456L, 789F, "streamFaviconUrl2", "streamSourceUrl2", "streamTitle2",
				"streamUrl2", "title2", "titleId2", updated2, "url2", "userId2",
				(ActivityImpl)activities.get(1), mediaItem);
		tx.commit();
		//
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		activities = mediaItem.getActivities();
		activity = (ActivityImpl)activities.get(0);
		activity.setAppId("appId3");
		activity.setBody("body3");
		activity.setBodyId("bodyId3");
		activity.setExternalId("externalId3");
		activity.setId("id3");
		activity.setPostedTime(321L);
		activity.setPriority(987F);
		activity.setStreamFaviconUrl("streamFaviconUrl3");
		activity.setStreamSourceUrl("streamSourceUrl3");
		activity.setStreamTitle("streamTitle3");
		activity.setStreamUrl("streamUrl3");
		activity.setTitle("title3");
		activity.setTitleId("titleId3");
		Date updated3 = new Date();
		activity.setUpdated(updated3);
		activity.setUrl("url3");
		activity.setUserId("userId3");
		activity = (ActivityImpl)activities.get(1);
		activities.remove(activity);
		activity.getMediaItems().remove(mediaItem);
		Date updated4 = new Date();
		activity = createActivity("appId4", "body4", "bodyId4", "externalId4",
				"id4", 791L, 135F, "streamFaviconUrl4", "streamSourceUrl4", "streamTitle4",
				"streamUrl4", "title4", "titleId4", updated4, "url4", "userId4");
		activities.add(activity);
		activity.getMediaItems().add(mediaItem);
		session.update(mediaItem);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		activities = mediaItem.getActivities();
		assertEquals("activities.size", 2, activities.size());
		assertActivity("appId3", "body3", "bodyId3", "externalId3",
				"id3", 321L, 987F, "streamFaviconUrl3", "streamSourceUrl3", "streamTitle3",
				"streamUrl3", "title3", "titleId3", updated3, "url3", "userId3",
				(ActivityImpl)activities.get(0), mediaItem);
		assertActivity("appId4", "body4", "bodyId4", "externalId4",
				"id4", 791L, 135F, "streamFaviconUrl4", "streamSourceUrl4", "streamTitle4",
				"streamUrl4", "title4", "titleId4", updated4, "url4", "userId4",
				(ActivityImpl)activities.get(1), mediaItem);
		tx.commit();
	}
	
	private MediaItemImpl createMediaItem() {
		MediaItemImpl mediaItem = new MediaItemImpl();
		mediaItem.setMimeType("mimeType1");
		mediaItem.setType(Type.AUDIO);
		mediaItem.setUrl("url1");
		return mediaItem;
	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		MediaItemImpl mediaItem = new MediaItemImpl();
		mediaItem.setMimeType("mimeType1");
		mediaItem.setType(Type.AUDIO);
		mediaItem.setUrl("url1");
		session.save(mediaItem);
		tx.commit();
		long oid = mediaItem.getObjectId();
		session.clear();
		//
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		tx.commit();
		assertEquals("mimeType", "mimeType1", mediaItem.getMimeType());
		assertEquals("type", Type.AUDIO, mediaItem.getType());
		assertEquals("url", "url1", mediaItem.getUrl());
		//
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		mediaItem.setMimeType("mimeType2");
		mediaItem.setType(Type.IMAGE);
		mediaItem.setUrl("url2");
		session.update(mediaItem);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		tx.commit();
		assertEquals("mimeType", "mimeType2", mediaItem.getMimeType());
		assertEquals("type", Type.IMAGE, mediaItem.getType());
		assertEquals("url", "url2", mediaItem.getUrl());
		//
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		session.delete(mediaItem);
		tx.commit();
		tx = session.beginTransaction();
		mediaItem = (MediaItemImpl)session.get(MediaItemImpl.class, oid);
		assertNull(mediaItem);
		tx.commit();
	}
	
}
