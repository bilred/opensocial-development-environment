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
package org.apache.shindig.social.opensocial.hibernate.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.BasicSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.hibernate.entities.ActivityImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.MediaItemImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActivityServiceImplTest extends AbstractServiceTest {
	
	private ActivityServiceImpl target;

	@Before
	public void setUpTarget() {
		target = new ActivityServiceImpl();
	}
	
	@After
	public void tearDownTarget() {
		target = null;
	}
	
	@Test
	public void testGetActivities1() throws Exception {
		Transaction tx = session.beginTransaction();
		Activity activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id1");
		activity.setUserId("userId1");
		session.save(activity);
		activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id2");
		activity.setUserId("userId2");
		session.save(activity);
		tx.commit();
		//
		UserId userId1 = new UserId(UserId.Type.userId, "userId1");
		UserId userId2 = new UserId(UserId.Type.userId, "userId2");
		Set<UserId> userIds = new HashSet<UserId>();
		userIds.add(userId1);
		userIds.add(userId2);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "appId1";
		SecurityToken token = new BasicSecurityToken("userId1", "userId1", "appId1", null, null, null, null, null);
		Future<RestfulCollection<Activity>> future = target.getActivities(userIds, groupId, appId, null, null, token);
		RestfulCollection<Activity> collection = future.get();
		assertEquals("collection.startIndex", 0, collection.getStartIndex());
		assertEquals("collection.totalResults", 2, collection.getTotalResults());
		List<Activity> entry = collection.getEntry();
		assertEquals("entry.size", 2, entry.size());
		assertEquals("entry[0].id", "id1", entry.get(0).getId());
		assertEquals("entry[1].id", "id2", entry.get(1).getId());
	}

	@Test
	public void testGetActivities2() throws Exception {
		Transaction tx = session.beginTransaction();
		Activity activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id1");
		activity.setUserId("userId1");
		session.save(activity);
		activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id2");
		activity.setUserId("userId2");
		session.save(activity);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.userId, "userId1");
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "appId1";
		SecurityToken token = new BasicSecurityToken("userId1", "userId1", "appId1", null, null, null, null, null);
		Set<String> activityIds = new HashSet<String>();
		activityIds.add("id1");
		Future<RestfulCollection<Activity>> future = target.getActivities(userId, groupId, appId, null, null, activityIds, token);
		RestfulCollection<Activity> collection = future.get();
		assertEquals("collection.startIndex", 0, collection.getStartIndex());
		assertEquals("collection.totalResults", 1, collection.getTotalResults());
		List<Activity> entry = collection.getEntry();
		assertEquals("entry.size", 1, entry.size());
		assertEquals("entry[0].id", "id1", entry.get(0).getId());
	}

	@Test
	public void testGetActivity() throws Exception {
		Transaction tx = session.beginTransaction();
		Activity activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id1");
		activity.setUserId("userId1");
		session.save(activity);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "appId1";
		SecurityToken token = new BasicSecurityToken("userId1", "userId1", "appId1", null, null, null, null, null);
		String activityId = "id1";
		Future<Activity> future = target.getActivity(userId, groupId, appId, null, activityId, token);
		activity = future.get();
		assertNotNull(activity);
		assertEquals("activity.id", "id1", activity.getId());
		assertEquals("activity.appId", "appId1", activity.getAppId());
		assertEquals("activity.userId", "userId1", activity.getUserId());
	}
	
	@Test
	public void testDeleteActivities() throws Exception {
		Transaction tx = session.beginTransaction();
		ActivityImpl activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id1");
		activity.setUserId("userId1");
		session.save(activity);
		activity = new ActivityImpl();
		activity.setAppId("appId1");
		activity.setId("id2");
		activity.setUserId("userId1");
		session.save(activity);
		activity = new ActivityImpl();
		activity.setAppId("appId2");
		activity.setId("id3");
		activity.setUserId("userId1");
		session.save(activity);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "appId1";
		SecurityToken token = new BasicSecurityToken("userId1", "userId1", "appId1", null, null, null, null, null);
		Set<String> activityIds = new HashSet<String>();
		activityIds.add("id1");
		activityIds.add("id2");
		target.deleteActivities(userId, groupId, appId, activityIds, token);
		//
		session.clear();
		Query query = session.createQuery("select a from ActivityImpl a");
		List<Activity> activities = (List<Activity>)query.list();
		assertEquals("activities.size", 1, activities.size());
		assertEquals("activities[0].id", "id3", activities.get(0).getId());
	}
	
	@Test
	public void testCreateActivity() throws Exception {
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		Activity activity = new ActivityImpl();
		activity.setBody("body1");
		activity.setBodyId("bodyId1");
		activity.setExternalId("externalId1");
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
		MediaItem mediaItem = new MediaItemImpl();
		mediaItem.setMimeType("mimeType1");
		mediaItem.setType(MediaItem.Type.AUDIO);
		mediaItem.setUrl("url1");
		activity.getMediaItems().add(mediaItem);
		activity.getTemplateParams().put("key1", "value1");
		target.createActivity(userId, groupId, appId, fields, activity, token);
		//
		session.clear();
		Query query = session.createQuery("select a from ActivityImpl a");
		activity = (Activity)query.uniqueResult();
		assertEquals("appId", "id1", activity.getAppId());
		assertEquals("body", "body1", activity.getBody());
		assertEquals("bodyId", "bodyId1", activity.getBodyId());
		assertEquals("externalId", "externalId1", activity.getExternalId());
		assertEquals("priority", new Float(123F), activity.getPriority());
		assertEquals("streamFaviconUrl", "streamFaviconUrl1", activity.getStreamFaviconUrl());
		assertEquals("streamSourceUrl", "streamSourceUrl1", activity.getStreamSourceUrl());
		assertEquals("streamTitle", "streamTitle1", activity.getStreamTitle());
		assertEquals("streamUrl", "streamUrl1", activity.getStreamUrl());
		assertEquals("title", "title1", activity.getTitle());
		assertEquals("titleId", "titleId1", activity.getTitleId());
		assertTimestamp("updated", updated, activity.getUpdated());
		assertEquals("url", "url1", activity.getUrl());
		assertEquals("userId", "id1", activity.getUserId());
		query = session.createQuery("select m from MediaItemImpl m");
		mediaItem = (MediaItem)query.uniqueResult();
		assertEquals("mimeType", "mimeType1", mediaItem.getMimeType());
		assertEquals("type", MediaItem.Type.AUDIO, mediaItem.getType());
		assertEquals("url", "url1", mediaItem.getUrl());
		assertEquals("templateParams[0]", "value1", activity.getTemplateParams().get("key1"));
	}

}
