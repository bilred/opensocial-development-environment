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

import org.apache.shindig.social.opensocial.model.Enum.Smoker;
import org.hibernate.Transaction;
import org.junit.Test;

public class SmokerImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		SmokerImpl smoker = new SmokerImpl();
//		smoker.setDisplayValue("displayValue1");
//		smoker.setValue(Smoker.HEAVILY);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		smoker.setPerson(person);
//		session.save(smoker);
//		long oid = smoker.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
//		person = (PersonImpl)smoker.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
//		person = (PersonImpl)smoker.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(smoker);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
//		person = (PersonImpl)smoker.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
//		smoker.setPerson(null);
//		session.update(smoker);
//		tx.commit();
//		tx = session.beginTransaction();
//		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
//		person = (PersonImpl)smoker.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		SmokerImpl smoker = new SmokerImpl();
		smoker.setDisplayValue("displayValue1");
		smoker.setValue(Smoker.HEAVILY);
		session.save(smoker);
		long oid = smoker.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
		assertEquals("displayValue", "displayValue1", smoker.getDisplayValue());
		assertEquals("value", Smoker.HEAVILY, smoker.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
		smoker.setDisplayValue("displayValue2");
		smoker.setValue(Smoker.NO);
		session.update(smoker);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
		assertEquals("displayValue", "displayValue2", smoker.getDisplayValue());
		assertEquals("value", Smoker.NO, smoker.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
		session.delete(smoker);
		tx.commit();
		tx = session.beginTransaction();
		smoker = (SmokerImpl)session.get(SmokerImpl.class, oid);
		assertNull(smoker);
		tx.commit();
	}

}
