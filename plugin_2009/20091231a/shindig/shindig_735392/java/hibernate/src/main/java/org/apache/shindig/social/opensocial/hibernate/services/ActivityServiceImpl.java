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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.opensocial.hibernate.entities.ActivityImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.MediaItemImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ActivityServiceImpl extends AbstractServiceImpl implements ActivityService {

	public Future<Void> createActivity(UserId userId, GroupId groupId, String appId, Set<String> fields, Activity activity, SecurityToken token) throws SocialSpiException {
		createActivity(userId.getUserId(token), activity, token.getAppId());
		return ImmediateFuture.newInstance(null);
	}

	private void createActivity(String userId, Activity activity, String appId) {
		Session session = HibernateUtils.currentSession();
		Transaction tx = session.beginTransaction();
		Activity entity = createActivityEntity(activity, userId, appId);
		session.save(entity);
		tx.commit();
	}
	
	private Activity createActivityEntity(Activity source, String userId, String appId) {
		Activity activity = new ActivityImpl();
		activity.setAppId(appId);
		activity.setBody(source.getBody());
		activity.setBodyId(source.getBodyId());
		activity.setExternalId(source.getExternalId());
		activity.setId(UUID.randomUUID().toString());
		activity.setPostedTime(System.currentTimeMillis());
		activity.setPriority(source.getPriority());
		activity.setStreamFaviconUrl(source.getStreamFaviconUrl());
		activity.setStreamSourceUrl(source.getStreamSourceUrl());
		activity.setStreamTitle(source.getStreamTitle());
		activity.setStreamUrl(source.getStreamUrl());
		activity.setTitle(source.getTitle());
		activity.setTitleId(source.getTitleId());
		activity.setUpdated(new Date());
		activity.setUrl(source.getUrl());
		activity.setUserId(userId);
		List<MediaItem> mediaItems = source.getMediaItems();
		List<MediaItem> newMediaItems = activity.getMediaItems();
		for (MediaItem mediaItem : mediaItems) {
			MediaItemImpl newMediaItem = new MediaItemImpl();
			newMediaItem.setMimeType(mediaItem.getMimeType());
			newMediaItem.setType(mediaItem.getType());
			newMediaItem.setUrl(mediaItem.getUrl());
			newMediaItems.add(newMediaItem);
		}
		Map<String, String> templateParams = source.getTemplateParams();
		if (templateParams != null) {
			activity.getTemplateParams().putAll(templateParams);
		}
		return activity;
	}

	public Future<Void> deleteActivities(UserId userId, GroupId groupId, String appId, Set<String> activityIds, SecurityToken token) throws SocialSpiException {
		Session session = HibernateUtils.currentSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("select a from ActivityImpl a where a.userId = :userId and a.appId = :appId and a.id in (:activityIds)");
		query.setParameter("userId", userId.getUserId(token));
		query.setParameter("appId", token.getAppId());
		query.setParameterList("activityIds", activityIds);
		List<ActivityImpl> activities = (List<ActivityImpl>)query.list();
		for (ActivityImpl activity : activities) {
			session.delete(activity);
		}
		tx.commit();
		return ImmediateFuture.newInstance(null);
	}

	public Future<Activity> getActivity(UserId userId, GroupId groupId, String appId, Set<String> fields, String activityId, SecurityToken token) throws SocialSpiException {
		Session session = HibernateUtils.currentSession();
		Query query = session.createQuery("select a from ActivityImpl a where a.userId = :userId and a.appId = :appId and a.id = :activityId");
		query.setParameter("userId", userId.getUserId(token));
		query.setParameter("appId", token.getAppId());
		query.setParameter("activityId", activityId);
		Activity activity = (Activity)query.uniqueResult();
		return ImmediateFuture.newInstance(activity);
	}

	public Future<RestfulCollection<Activity>> getActivities(UserId userId,
			GroupId groupId, String appId, Set<String> fields,
			CollectionOptions options, Set<String> activityIds, SecurityToken token)
			throws SocialSpiException {
		Session session = HibernateUtils.currentSession();
		Set<String> ids = getIdSet(userId, groupId, token);
		Query query = session.createQuery("select a from ActivityImpl a where a.userId in (:ids) and a.appId = :appId and a.id in (:activityIds)");
		query.setParameterList("ids", ids);
		query.setParameter("appId", appId);
		query.setParameterList("activityIds", activityIds);
		List<Activity> activities = (List<Activity>)query.list();
		RestfulCollection<Activity> result = new RestfulCollection<Activity>(activities);
		return ImmediateFuture.newInstance(result);
	}

	public Future<RestfulCollection<Activity>> getActivities(
			Set<UserId> userIds, GroupId groupId, String appId,
			Set<String> fields, CollectionOptions options, SecurityToken token)
			throws SocialSpiException {
		Session session = HibernateUtils.currentSession();
		Set<String> ids = new TreeSet<String>();
		for (UserId userId : userIds) {
			ids.addAll(getIdSet(userId, groupId, token));
		}
		Query query = session.createQuery("select a from ActivityImpl a where a.userId in (:ids) and a.appId = :appId");
		query.setParameterList("ids", ids);
		query.setParameter("appId", appId);
		List<Activity> activities = (List<Activity>)query.list();
		RestfulCollection<Activity> result = new RestfulCollection<Activity>(activities);
		return ImmediateFuture.newInstance(result);
	}

}
