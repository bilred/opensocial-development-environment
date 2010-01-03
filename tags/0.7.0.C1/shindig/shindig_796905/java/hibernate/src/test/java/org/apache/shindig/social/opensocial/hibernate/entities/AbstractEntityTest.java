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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractEntityTest {

	protected Session session;
	
	@Before
	public void createSession() throws Exception {
		SessionFactory sessionFactory = new AnnotationConfiguration().configure("hibernate_for_test.cfg.xml").buildSessionFactory();
		session = sessionFactory.openSession();
	}
	
	@After
	public void closeSession() throws Exception {
		if (session != null) {
			session.close();
		}
	}
	
	protected ActivityImpl createActivity(String appId, String body, String bodyId, String externalId,
			String id, long postedTime, float priority, String streamFaviconUrl, String streamSourceUrl,
			String streamTitle, String streamUrl, String title, String titleId, Date updated,
			String url, String userId) {
		ActivityImpl activity = new ActivityImpl();
		activity.setAppId(appId);
		activity.setBody(body);
		activity.setBodyId(bodyId);
		activity.setExternalId(externalId);
		activity.setId(id);
		activity.setPostedTime(postedTime);
		activity.setPriority(priority);
		activity.setStreamFaviconUrl(streamFaviconUrl);
		activity.setStreamSourceUrl(streamSourceUrl);
		activity.setStreamTitle(streamTitle);
		activity.setStreamUrl(streamUrl);
		activity.setTitle(title);
		activity.setTitleId(titleId);
		activity.setUpdated(updated);
		activity.setUrl(url);
		activity.setUserId(userId);
		return activity;
	}
	
	protected void assertActivity(String appId, String body, String bodyId, String externalId,
			String id, long postedTime, float priority, String streamFaviconUrl, String streamSourceUrl,
			String streamTitle, String streamUrl, String title, String titleId, Date updated,
			String url, String userId, ActivityImpl activity, MediaItemImpl mediaItem) {
		assertEquals("appId", appId, activity.getAppId());
		assertEquals("body", body, activity.getBody());
		assertEquals("bodyId", bodyId, activity.getBodyId());
		assertEquals("externalId", externalId, activity.getExternalId());
		assertEquals("id", id, activity.getId());
		assertEquals("postedTime", new Long(postedTime), activity.getPostedTime());
		assertEquals("priority", new Float(priority), activity.getPriority());
		assertEquals("streamFaviconUrl", streamFaviconUrl, activity.getStreamFaviconUrl());
		assertEquals("streamSourceUrl", streamSourceUrl, activity.getStreamSourceUrl());
		assertEquals("streamTitle", streamTitle, activity.getStreamTitle());
		assertEquals("streamUrl", streamUrl, activity.getStreamUrl());
		assertEquals("title", title, activity.getTitle());
		assertEquals("titleId", titleId, activity.getTitleId());
		assertEquals("updated", updated, activity.getUpdated());
		assertEquals("url", url, activity.getUrl());
		assertEquals("userId", userId, activity.getUserId());
		assertEquals("mediaItem", mediaItem, activity.getMediaItems().get(0));
	}

	protected void assertActivity(String appId, String body, String bodyId, String externalId,
			String id, long postedTime, float priority, String streamFaviconUrl, String streamSourceUrl,
			String streamTitle, String streamUrl, String title, String titleId, Date updated,
			String url, String userId, ActivityImpl activity) {
		assertEquals("appId", appId, activity.getAppId());
		assertEquals("body", body, activity.getBody());
		assertEquals("bodyId", bodyId, activity.getBodyId());
		assertEquals("externalId", externalId, activity.getExternalId());
		assertEquals("id", id, activity.getId());
		assertEquals("postedTime", new Long(postedTime), activity.getPostedTime());
		assertEquals("priority", new Float(priority), activity.getPriority());
		assertEquals("streamFaviconUrl", streamFaviconUrl, activity.getStreamFaviconUrl());
		assertEquals("streamSourceUrl", streamSourceUrl, activity.getStreamSourceUrl());
		assertEquals("streamTitle", streamTitle, activity.getStreamTitle());
		assertEquals("streamUrl", streamUrl, activity.getStreamUrl());
		assertEquals("title", title, activity.getTitle());
		assertEquals("titleId", titleId, activity.getTitleId());
		assertEquals("updated", updated, activity.getUpdated());
		assertEquals("url", url, activity.getUrl());
		assertEquals("userId", userId, activity.getUserId());
	}
	
	protected void assertDate(String message, Date expected, Date actual) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(expected);
		int expectedYear = cal.get(Calendar.YEAR);
		int expectedMonth = cal.get(Calendar.MONTH);
		int expectedDate = cal.get(Calendar.DATE);
		cal.setTime(actual);
		int actualYear = cal.get(Calendar.YEAR);
		int actualMonth = cal.get(Calendar.MONTH);
		int actualDate = cal.get(Calendar.DATE);
		assertEquals(message + "-year", expectedYear, actualYear);
		assertEquals(message + "-month", expectedMonth, actualMonth);
		assertEquals(message + "-date", expectedDate, actualDate);
	}

	protected void assertTimestamp(String message, Date expected, Date actual) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(expected);
		int expectedYear = cal.get(Calendar.YEAR);
		int expectedMonth = cal.get(Calendar.MONTH);
		int expectedDate = cal.get(Calendar.DATE);
		int expectedHour = cal.get(Calendar.HOUR_OF_DAY);
		int expectedMinute = cal.get(Calendar.MINUTE);
		int expectedSecond = cal.get(Calendar.SECOND);
		cal.setTime(actual);
		int actualYear = cal.get(Calendar.YEAR);
		int actualMonth = cal.get(Calendar.MONTH);
		int actualDate = cal.get(Calendar.DATE);
		int actualHour = cal.get(Calendar.HOUR_OF_DAY);
		int actualMinute = cal.get(Calendar.MINUTE);
		int actualSecond = cal.get(Calendar.SECOND);
		assertEquals(message + "-year", expectedYear, actualYear);
		assertEquals(message + "-month", expectedMonth, actualMonth);
		assertEquals(message + "-date", expectedDate, actualDate);
		assertEquals(message + "-hour", expectedHour, actualHour);
		assertEquals(message + "-minute", expectedMinute, actualMinute);
		assertEquals(message + "-second", expectedSecond, actualSecond);
	}

}
