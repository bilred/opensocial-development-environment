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

public class ApplicationImplTest extends AbstractEntityTest {

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		application.setTitle("title1");
		application.setPath("path1");
		application.setConsumerKey("consumerKey1");
		application.setConsumerSecret("consumerSecret1");
		session.save(application);
		tx.commit();
		long oid = application.getObjectId();
		//
		session.clear();
		tx = session.beginTransaction();
		application = (ApplicationImpl)session.get(ApplicationImpl.class, oid);
		assertEquals("id", "id1", application.getId());
		assertEquals("title", "title1", application.getTitle());
		assertEquals("path", "path1", application.getPath());
		assertEquals("consumerKey", "consumerKey1", application.getConsumerKey());
		assertEquals("consumerSecret", "consumerSecret1", application.getConsumerSecret());
		tx.commit();
		//
		tx = session.beginTransaction();
		application = (ApplicationImpl)session.get(ApplicationImpl.class, oid);
		application.setId("id2");
		application.setTitle("title2");
		application.setPath("path2");
		application.setConsumerKey("consumerKey2");
		application.setConsumerSecret("consumerSecret2");
		session.update(application);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		application = (ApplicationImpl)session.get(ApplicationImpl.class, oid);
		tx.commit();
		assertEquals("id", "id2", application.getId());
		assertEquals("title", "title2", application.getTitle());
		assertEquals("path", "path2", application.getPath());
		assertEquals("consumerKey", "consumerKey2", application.getConsumerKey());
		assertEquals("consumerSecret", "consumerSecret2", application.getConsumerSecret());
		//
		tx = session.beginTransaction();
		application = (ApplicationImpl)session.get(ApplicationImpl.class, oid);
		session.delete(application);
		tx.commit();
		tx = session.beginTransaction();
		application = (ApplicationImpl)session.get(ApplicationImpl.class, oid);
		assertNull(application);
		tx.commit();
	}

}
