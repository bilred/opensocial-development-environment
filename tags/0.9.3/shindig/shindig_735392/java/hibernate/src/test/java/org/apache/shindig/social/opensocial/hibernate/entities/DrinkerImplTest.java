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

import org.apache.shindig.social.opensocial.model.Enum.Drinker;
import org.hibernate.Transaction;
import org.junit.Test;

public class DrinkerImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		DrinkerImpl drinker = new DrinkerImpl();
//		drinker.setDisplayValue("displayValue1");
//		drinker.setValue(Drinker.HEAVILY);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		drinker.setPerson(person);
//		session.save(drinker);
//		long oid = drinker.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
//		person = (PersonImpl)drinker.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
//		person = (PersonImpl)drinker.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(drinker);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
//		person = (PersonImpl)drinker.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
//		drinker.setPerson(null);
//		session.update(drinker);
//		tx.commit();
//		tx = session.beginTransaction();
//		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
//		person = (PersonImpl)drinker.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		DrinkerImpl drinker = new DrinkerImpl();
		drinker.setDisplayValue("displayValue1");
		drinker.setValue(Drinker.HEAVILY);
		session.save(drinker);
		long oid = drinker.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
		assertEquals("displayValue", "displayValue1", drinker.getDisplayValue());
		assertEquals("value", Drinker.HEAVILY, drinker.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
		drinker.setDisplayValue("displayValue2");
		drinker.setValue(Drinker.NO);
		session.update(drinker);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
		assertEquals("displayValue", "displayValue2", drinker.getDisplayValue());
		assertEquals("value", Drinker.NO, drinker.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
		session.delete(drinker);
		tx.commit();
		tx = session.beginTransaction();
		drinker = (DrinkerImpl)session.get(DrinkerImpl.class, oid);
		assertNull(drinker);
		tx.commit();
	}

}
