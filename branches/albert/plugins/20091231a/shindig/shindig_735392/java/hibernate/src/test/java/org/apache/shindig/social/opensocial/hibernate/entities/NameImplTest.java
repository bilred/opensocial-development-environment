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

public class NameImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		NameImpl name = new NameImpl();
//		name.setAdditionalName("additionalName1");
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		name.setPerson(person);
//		session.save(name);
//		long oid = name.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		name = (NameImpl)session.get(NameImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (NameImpl)session.get(NameImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(name);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		name = (NameImpl)session.get(NameImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (NameImpl)session.get(NameImpl.class, oid);
//		name.setPerson(null);
//		session.update(name);
//		tx.commit();
//		tx = session.beginTransaction();
//		name = (NameImpl)session.get(NameImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		NameImpl name = new NameImpl();
		name.setAdditionalName("additionalName1");
		name.setFamilyName("familyName1");
		name.setGivenName("givenName1");
		name.setHonorificPrefix("honorificPrefix1");
		name.setHonorificSuffix("honorificSuffix1");
		name.setUnstructured("unstructured1");
		session.save(name);
		long oid = name.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		name = (NameImpl)session.get(NameImpl.class, oid);
		assertEquals("additionalName", "additionalName1", name.getAdditionalName());
		assertEquals("familyName", "familyName1", name.getFamilyName());
		assertEquals("givenName", "givenName1", name.getGivenName());
		assertEquals("honorificPrefix", "honorificPrefix1", name.getHonorificPrefix());
		assertEquals("honorificSuffix", "honorificSuffix1", name.getHonorificSuffix());
		assertEquals("unstructured", "unstructured1", name.getUnstructured());
		tx.commit();
		//
		tx = session.beginTransaction();
		name.setAdditionalName("additionalName2");
		name.setFamilyName("familyName2");
		name.setGivenName("givenName2");
		name.setHonorificPrefix("honorificPrefix2");
		name.setHonorificSuffix("honorificSuffix2");
		name.setUnstructured("unstructured2");
		session.update(name);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		name = (NameImpl)session.get(NameImpl.class, oid);
		assertEquals("additionalName", "additionalName2", name.getAdditionalName());
		assertEquals("familyName", "familyName2", name.getFamilyName());
		assertEquals("givenName", "givenName2", name.getGivenName());
		assertEquals("honorificPrefix", "honorificPrefix2", name.getHonorificPrefix());
		assertEquals("honorificSuffix", "honorificSuffix2", name.getHonorificSuffix());
		assertEquals("unstructured", "unstructured2", name.getUnstructured());
		tx.commit();
		//
		tx = session.beginTransaction();
		name = (NameImpl)session.get(NameImpl.class, oid);
		session.delete(name);
		tx.commit();
		tx = session.beginTransaction();
		name = (NameImpl)session.get(NameImpl.class, oid);
		assertNull(name);
		tx.commit();
	}

}
