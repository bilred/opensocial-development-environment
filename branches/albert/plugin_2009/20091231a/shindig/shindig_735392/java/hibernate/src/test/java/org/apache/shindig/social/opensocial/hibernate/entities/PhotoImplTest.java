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

public class PhotoImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		PhotoImpl photo = new PhotoImpl();
//		photo.setValue("value1");
//		photo.setType("type1");
//		photo.setPrimary(true);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		photo.setPerson(person);
//		session.save(photo);
//		long oid = photo.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
//		person = (PersonImpl)photo.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
//		person = (PersonImpl)photo.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(photo);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
//		person = (PersonImpl)photo.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
//		photo.setPerson(null);
//		session.update(photo);
//		tx.commit();
//		tx = session.beginTransaction();
//		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
//		person = (PersonImpl)photo.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		PhotoImpl photo = new PhotoImpl();
		photo.setValue("value1");
		photo.setType("type1");
		photo.setPrimary(true);
		session.save(photo);
		long oid = photo.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
		assertEquals("value", "value1", photo.getValue());
		assertEquals("type", "type1", photo.getType());
		assertEquals("primary", new Boolean(true), photo.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		photo.setValue("value2");
		photo.setType("type2");
		photo.setPrimary(false);
		session.update(photo);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
		assertEquals("value", "value2", photo.getValue());
		assertEquals("type", "type2", photo.getType());
		assertEquals("primary", new Boolean(false), photo.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
		session.delete(photo);
		tx.commit();
		tx = session.beginTransaction();
		photo = (PhotoImpl)session.get(PhotoImpl.class, oid);
		assertNull(photo);
		tx.commit();
	}

}
