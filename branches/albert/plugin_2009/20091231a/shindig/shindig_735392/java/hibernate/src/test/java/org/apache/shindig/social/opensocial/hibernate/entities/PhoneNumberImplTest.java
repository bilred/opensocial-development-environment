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

public class PhoneNumberImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		PhoneNumberImpl phoneNumber = new PhoneNumberImpl();
//		phoneNumber.setValue("value1");
//		phoneNumber.setType("type1");
//		phoneNumber.setPrimary(true);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		phoneNumber.setPerson(person);
//		session.save(phoneNumber);
//		long oid = phoneNumber.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
//		person = (PersonImpl)phoneNumber.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
//		person = (PersonImpl)phoneNumber.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(phoneNumber);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
//		person = (PersonImpl)phoneNumber.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
//		phoneNumber.setPerson(null);
//		session.update(phoneNumber);
//		tx.commit();
//		tx = session.beginTransaction();
//		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
//		person = (PersonImpl)phoneNumber.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		PhoneNumberImpl phoneNumber = new PhoneNumberImpl();
		phoneNumber.setValue("value1");
		phoneNumber.setType("type1");
		phoneNumber.setPrimary(true);
		session.save(phoneNumber);
		long oid = phoneNumber.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
		assertEquals("value", "value1", phoneNumber.getValue());
		assertEquals("type", "type1", phoneNumber.getType());
		assertEquals("primary", new Boolean(true), phoneNumber.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		phoneNumber.setValue("value2");
		phoneNumber.setType("type2");
		phoneNumber.setPrimary(false);
		session.update(phoneNumber);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
		assertEquals("value", "value2", phoneNumber.getValue());
		assertEquals("type", "type2", phoneNumber.getType());
		assertEquals("primary", new Boolean(false), phoneNumber.getPrimary());
		tx.commit();
		//
		tx = session.beginTransaction();
		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
		session.delete(phoneNumber);
		tx.commit();
		tx = session.beginTransaction();
		phoneNumber = (PhoneNumberImpl)session.get(PhoneNumberImpl.class, oid);
		assertNull(phoneNumber);
		tx.commit();
	}

}
