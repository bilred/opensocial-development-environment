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

import java.util.Map;

import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Transaction;
import org.junit.Test;

public class ApplicationDataMapImplTest extends AbstractEntityTest {
	
	@Test
	public void testDataMap() throws Exception {
		Transaction tx = session.beginTransaction();
		ApplicationDataMapImpl applicationDataMap = new ApplicationDataMapImpl();
		Map<String, String> dataMap = applicationDataMap.getDataMap();
		dataMap.put("key1", "value1");
		dataMap.put("key2", "value2");
		session.save(applicationDataMap);
		long oid = applicationDataMap.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		dataMap = applicationDataMap.getDataMap();
		assertEquals("dataMap.size", 2, dataMap.size());
		assertEquals("dataMap[key1]", "value1", dataMap.get("key1"));
		assertEquals("dataMap[key2]", "value2", dataMap.get("key2"));
		tx.commit();
		//
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		dataMap = applicationDataMap.getDataMap();
		dataMap.remove("key1");
		dataMap.put("key2", "value3");
		dataMap.put("key3", "value4");
		session.update(applicationDataMap);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		dataMap = applicationDataMap.getDataMap();
		assertEquals("dataMap.size", 2, dataMap.size());
		assertNull("dataMap[key1]", dataMap.get("key1"));
		assertEquals("dataMap[key2]", "value3", dataMap.get("key2"));
		assertEquals("dataMap[key3]", "value4", dataMap.get("key3"));
		tx.commit();
	}

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		ApplicationDataMapImpl applicationDataMap = new ApplicationDataMapImpl();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		applicationDataMap.setPerson(person1);
		ApplicationImpl application1 = new ApplicationImpl();
		application1.setId("id1");
		applicationDataMap.setApplication(application1);
		session.save(applicationDataMap);
		tx.commit();
		long oid = applicationDataMap.getObjectId();
		//
		session.clear();
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		assertEquals("person.id", "id1", applicationDataMap.getPerson().getId());
		assertEquals("application.id", "id1", applicationDataMap.getApplication().getId());
		tx.commit();
		//
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		applicationDataMap.getPerson().setId("id2");
		applicationDataMap.getApplication().setId("id2");
		session.update(applicationDataMap);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		tx.commit();
		assertEquals("person.id", "id2", applicationDataMap.getPerson().getId());
		assertEquals("application.id", "id2", applicationDataMap.getApplication().getId());
		//
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		session.delete(applicationDataMap);
		tx.commit();
		tx = session.beginTransaction();
		applicationDataMap = (ApplicationDataMapImpl)session.get(ApplicationDataMapImpl.class, oid);
		assertNull(applicationDataMap);
		tx.commit();
	}

}
