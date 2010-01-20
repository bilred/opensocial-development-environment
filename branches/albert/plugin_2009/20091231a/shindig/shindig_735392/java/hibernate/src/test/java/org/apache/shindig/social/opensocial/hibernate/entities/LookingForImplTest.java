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

import org.apache.shindig.social.opensocial.model.Enum.LookingFor;
import org.hibernate.Transaction;
import org.junit.Test;

public class LookingForImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		LookingForImpl lookingFor = new LookingForImpl();
//		lookingFor.setDisplayValue("displayValue1");
//		lookingFor.setValue(LookingFor.ACTIVITY_PARTNERS);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		lookingFor.setPerson(person);
//		session.save(lookingFor);
//		long oid = lookingFor.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
//		person = (PersonImpl)lookingFor.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
//		person = (PersonImpl)lookingFor.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(lookingFor);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
//		person = (PersonImpl)lookingFor.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
//		lookingFor.setPerson(null);
//		session.update(lookingFor);
//		tx.commit();
//		tx = session.beginTransaction();
//		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
//		person = (PersonImpl)lookingFor.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		LookingForImpl lookingFor = new LookingForImpl();
		lookingFor.setDisplayValue("displayValue1");
		lookingFor.setValue(LookingFor.ACTIVITY_PARTNERS);
		session.save(lookingFor);
		long oid = lookingFor.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
		assertEquals("displayValue", "displayValue1", lookingFor.getDisplayValue());
		assertEquals("value", LookingFor.ACTIVITY_PARTNERS, lookingFor.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
		lookingFor.setDisplayValue("displayValue2");
		lookingFor.setValue(LookingFor.DATING);
		session.update(lookingFor);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
		assertEquals("displayValue", "displayValue2", lookingFor.getDisplayValue());
		assertEquals("value", LookingFor.DATING, lookingFor.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
		session.delete(lookingFor);
		tx.commit();
		tx = session.beginTransaction();
		lookingFor = (LookingForImpl)session.get(LookingForImpl.class, oid);
		assertNull(lookingFor);
		tx.commit();
	}

}
