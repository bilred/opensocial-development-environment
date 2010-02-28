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

public class ImImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		ImImpl im = new ImImpl();
//		im.setValue("value1");
//		im.setType("type1");
//		im.setPrimary(true);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		im.setPerson(person);
//		session.save(im);
//		long oid = im.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		im = (ImImpl)session.get(ImImpl.class, oid);
//		person = (PersonImpl)im.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		im = (ImImpl)session.get(ImImpl.class, oid);
//		person = (PersonImpl)im.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(im);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		im = (ImImpl)session.get(ImImpl.class, oid);
//		person = (PersonImpl)im.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		im = (ImImpl)session.get(ImImpl.class, oid);
//		im.setPerson(null);
//		session.update(im);
//		tx.commit();
//		tx = session.beginTransaction();
//		im = (ImImpl)session.get(ImImpl.class, oid);
//		person = (PersonImpl)im.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		ImImpl im = new ImImpl();
		im.setValue("value1");
		im.setType("type1");
		im.setPrimary(true);
		session.save(im);
		long oid = im.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		im = (ImImpl)session.get(ImImpl.class, oid);
		assertEquals("value", "value1", im.getValue());
		assertEquals("type", "type1", im.getType());
		assertEquals("primary", new Boolean(true), im.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		im.setValue("value2");
		im.setType("type2");
		im.setPrimary(false);
		session.update(im);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		im = (ImImpl)session.get(ImImpl.class, oid);
		assertEquals("value", "value2", im.getValue());
		assertEquals("type", "type2", im.getType());
		assertEquals("primary", new Boolean(false), im.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		im = (ImImpl)session.get(ImImpl.class, oid);
		session.delete(im);
		tx.commit();
		tx = session.beginTransaction();
		im = (ImImpl)session.get(ImImpl.class, oid);
		assertNull(im);
		tx.commit();
	}

}
