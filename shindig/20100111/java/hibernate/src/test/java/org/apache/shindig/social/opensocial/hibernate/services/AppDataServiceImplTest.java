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
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.BasicSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationDataMapImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppDataServiceImplTest extends AbstractServiceTest {
	
	private AppDataServiceImpl target;

	@Before
	public void setUpTarget() {
		target = new AppDataServiceImpl();
	}
	
	@After
	public void tearDownTarget() {
		target = null;
	}
	
	@Test
	public void testUpdatePersonDataForbidden() throws Exception {
		Transaction tx = session.beginTransaction();
		UserId userId = new UserId(UserId.Type.userId, "id2");
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		Map<String, String> values = new HashMap<String, String>();
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		try {
			target.updatePersonData(userId, groupId, appId, fields, values, token);
			fail("SocialSpiException not occurred.");
		} catch(ProtocolException eexpected) {
		}
		tx.commit();
	}
	
	@Test
	public void testUpdatePersonData() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		session.save(application);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		fields.add("key1");
		fields.add("key2");
		Map<String, String> values = new HashMap<String, String>();
		values.put("key1", "value1");
		values.put("key2", "value2");
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		target.updatePersonData(userId, groupId, appId, fields, values, token);
		//
		tx = session.beginTransaction();
		Query query = session.createQuery("select d from ApplicationDataMapImpl d");
		ApplicationDataMapImpl appData = (ApplicationDataMapImpl)query.uniqueResult();
		assertEquals("appData.person", "id1", appData.getPerson().getId());
		assertEquals("appData.application", "id1", appData.getApplication().getId());
		Map<String, String> dataMap = appData.getDataMap();
		assertEquals("dataMap.key1", "value1", dataMap.get("key1"));
		assertEquals("dataMap.key2", "value2", dataMap.get("key2"));
		tx.commit();
	}
	
	@Test
	public void testGetPersonData() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		PersonImpl person2 = new PersonImpl();
		person2.setId("id2");
		session.save(person2);
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		session.save(application);
		ApplicationDataMapImpl appData1 = new ApplicationDataMapImpl();
		appData1.setApplication(application);
		appData1.setPerson(person1);
		Map<String, String> dataMap = appData1.getDataMap();
		dataMap.put("key1", "id1-value1");
		dataMap.put("key2", "id1-value2");
		session.save(appData1);
		ApplicationDataMapImpl appData2 = new ApplicationDataMapImpl();
		appData2.setApplication(application);
		appData2.setPerson(person2);
		dataMap = appData2.getDataMap();
		dataMap.put("key1", "id2-value1");
		dataMap.put("key2", "id2-value2");
		session.save(appData2);
		tx.commit();
		//
		tx = session.beginTransaction();
		Set<UserId> userIds = new HashSet<UserId>();
		userIds.add(new UserId(UserId.Type.userId, "id1"));
		userIds.add(new UserId(UserId.Type.userId, "id2"));
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		fields.add("*");
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		Future<DataCollection> result = target.getPersonData(userIds, groupId, appId, fields, token);
		DataCollection dataCollection = result.get();
		Map<String, Map<String, String>> entry = dataCollection.getEntry();
		Map<String, String> map = entry.get("id1");
		assertEquals("map.size", 2, map.size());
		assertEquals("id1.key1", "id1-value1", map.get("key1"));
		assertEquals("id1.key2", "id1-value2", map.get("key2"));
		map = entry.get("id2");
		assertEquals("map.size", 2, map.size());
		assertEquals("id2.key1", "id2-value1", map.get("key1"));
		assertEquals("id2.key2", "id2-value2", map.get("key2"));
		fields = new HashSet<String>();
		fields.add("key2");
		result = target.getPersonData(userIds, groupId, appId, fields, token);
		dataCollection = result.get();
		entry = dataCollection.getEntry();
		map = entry.get("id1");
		assertEquals("map.size", 1, map.size());
		assertEquals("id1.key2", "id1-value2", map.get("key2"));
		map = entry.get("id2");
		assertEquals("map.size", 1, map.size());
		assertEquals("id2.key2", "id2-value2", map.get("key2"));
		tx.commit();
	}
	
	@Test
	public void testDeletePersonDataForbidden() throws Exception {
		Transaction tx = session.beginTransaction();
		UserId userId = new UserId(UserId.Type.userId, "id2");
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		try {
			target.deletePersonData(userId, groupId, appId, fields, token);
			fail("SocialSpiException not occurred.");
		} catch(ProtocolException eexpected) {
		}
		tx.commit();
	}
	
	@Test
	public void testDeletePersonData() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		session.save(application);
		ApplicationDataMapImpl appData = new ApplicationDataMapImpl();
		appData.setApplication(application);
		appData.setPerson(person1);
		Map<String, String> dataMap = appData.getDataMap();
		dataMap.put("key1", "id1-value1");
		dataMap.put("key2", "id1-value2");
		session.save(appData);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		fields.add("key1");
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		target.deletePersonData(userId, groupId, appId, fields, token);
		//
		tx = session.beginTransaction();
		Query query = session.createQuery("select d from ApplicationDataMapImpl d");
		appData = (ApplicationDataMapImpl)query.uniqueResult();
		assertEquals("appData.person", "id1", appData.getPerson().getId());
		assertEquals("appData.application", "id1", appData.getApplication().getId());
		dataMap = appData.getDataMap();
		assertEquals("map.size", 1, dataMap.size());
		assertEquals("dataMap.key2", "id1-value2", dataMap.get("key2"));
		tx.commit();
	}

	@Test
	public void testDeletePersonDataAll() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		session.save(application);
		ApplicationDataMapImpl appData = new ApplicationDataMapImpl();
		appData.setApplication(application);
		appData.setPerson(person1);
		Map<String, String> dataMap = appData.getDataMap();
		dataMap.put("key1", "id1-value1");
		dataMap.put("key2", "id1-value2");
		session.save(appData);
		tx.commit();
		//
		UserId userId = new UserId(UserId.Type.viewer, null);
		GroupId groupId = new GroupId(GroupId.Type.self, null);
		String appId = "id1";
		Set<String> fields = new HashSet<String>();
		fields.add("*");
		SecurityToken token = new BasicSecurityToken("id1", "id1", "id1", null, null, null, null, null);
		target.deletePersonData(userId, groupId, appId, fields, token);
		//
		tx = session.beginTransaction();
		Query query = session.createQuery("select d from ApplicationDataMapImpl d");
		appData = (ApplicationDataMapImpl)query.uniqueResult();
		assertEquals("appData.person", "id1", appData.getPerson().getId());
		assertEquals("appData.application", "id1", appData.getApplication().getId());
		dataMap = appData.getDataMap();
		assertEquals("map.size", 0, dataMap.size());
		tx.commit();
	}

}
