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
import static org.junit.Assert.assertNull;

import org.hibernate.Transaction;
import org.junit.Test;

public class UserPrefImplTest extends AbstractEntityTest {

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		UserPrefImpl userPref = new UserPrefImpl();
		userPref.setAppId("appId1");
		userPref.setName("name1");
		userPref.setValue("value1");
		userPref.setViewerId("viewerId1");
		session.save(userPref);
		tx.commit();
		long oid = userPref.getObjectId();
		//
		session.clear();
		tx = session.beginTransaction();
		userPref = (UserPrefImpl)session.get(UserPrefImpl.class, oid);
		assertEquals("appId", "appId1", userPref.getAppId());
		assertEquals("name", "name1", userPref.getName());
		assertEquals("value", "value1", userPref.getValue());
		assertEquals("viewerId", "viewerId1", userPref.getViewerId());
		tx.commit();
		//
		tx = session.beginTransaction();
		userPref = (UserPrefImpl)session.get(UserPrefImpl.class, oid);
		userPref.setAppId("appId2");
		userPref.setName("name2");
		userPref.setValue("value2");
		userPref.setViewerId("viewerId2");
		session.update(userPref);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		userPref = (UserPrefImpl)session.get(UserPrefImpl.class, oid);
		tx.commit();
		assertEquals("appId", "appId2", userPref.getAppId());
		assertEquals("name", "name2", userPref.getName());
		assertEquals("value", "value2", userPref.getValue());
		assertEquals("viewerId", "viewerId2", userPref.getViewerId());
		//
		tx = session.beginTransaction();
		userPref = (UserPrefImpl)session.get(UserPrefImpl.class, oid);
		session.delete(userPref);
		tx.commit();
		tx = session.beginTransaction();
		userPref = (UserPrefImpl)session.get(UserPrefImpl.class, oid);
		assertNull(userPref);
		tx.commit();
	}

}
