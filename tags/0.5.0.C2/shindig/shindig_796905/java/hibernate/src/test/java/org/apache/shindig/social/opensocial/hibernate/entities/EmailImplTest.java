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

public class EmailImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		EmailImpl email = new EmailImpl();
//		email.setValue("value1");
//		email.setType("type1");
//		email.setPrimary(true);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		email.setPerson(person);
//		session.save(email);
//		long oid = email.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		email = (EmailImpl)session.get(EmailImpl.class, oid);
//		person = (PersonImpl)email.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		email = (EmailImpl)session.get(EmailImpl.class, oid);
//		person = (PersonImpl)email.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(email);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		email = (EmailImpl)session.get(EmailImpl.class, oid);
//		person = (PersonImpl)email.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		email = (EmailImpl)session.get(EmailImpl.class, oid);
//		email.setPerson(null);
//		session.update(email);
//		tx.commit();
//		tx = session.beginTransaction();
//		email = (EmailImpl)session.get(EmailImpl.class, oid);
//		person = (PersonImpl)email.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		EmailImpl email = new EmailImpl();
		email.setValue("value1");
		email.setType("type1");
		email.setPrimary(true);
		session.save(email);
		long oid = email.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		email = (EmailImpl)session.get(EmailImpl.class, oid);
		assertEquals("value", "value1", email.getValue());
		assertEquals("type", "type1", email.getType());
		assertEquals("primary", new Boolean(true), email.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		email.setValue("value2");
		email.setType("type2");
		email.setPrimary(false);
		session.update(email);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		email = (EmailImpl)session.get(EmailImpl.class, oid);
		assertEquals("value", "value2", email.getValue());
		assertEquals("type", "type2", email.getType());
		assertEquals("primary", new Boolean(false), email.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		email = (EmailImpl)session.get(EmailImpl.class, oid);
		session.delete(email);
		tx.commit();
		tx = session.beginTransaction();
		email = (EmailImpl)session.get(EmailImpl.class, oid);
		assertNull(email);
		tx.commit();
	}

}
