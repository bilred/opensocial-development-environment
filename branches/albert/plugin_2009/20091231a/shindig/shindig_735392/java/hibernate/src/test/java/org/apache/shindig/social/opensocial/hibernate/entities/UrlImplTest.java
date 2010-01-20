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

public class UrlImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		UrlImpl url = new UrlImpl();
//		url.setValue("value1");
//		url.setType("type1");
//		url.setPrimary(true);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		url.setPerson(person);
//		session.save(url);
//		long oid = url.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		url = (UrlImpl)session.get(UrlImpl.class, oid);
//		person = (PersonImpl)url.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		url = (UrlImpl)session.get(UrlImpl.class, oid);
//		person = (PersonImpl)url.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(url);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		url = (UrlImpl)session.get(UrlImpl.class, oid);
//		person = (PersonImpl)url.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		url = (UrlImpl)session.get(UrlImpl.class, oid);
//		url.setPerson(null);
//		session.update(url);
//		tx.commit();
//		tx = session.beginTransaction();
//		url = (UrlImpl)session.get(UrlImpl.class, oid);
//		person = (PersonImpl)url.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		UrlImpl url = new UrlImpl();
		url.setValue("value1");
		url.setType("type1");
		url.setPrimary(true);
		url.setLinkText("linkText1");
		session.save(url);
		long oid = url.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		url = (UrlImpl)session.get(UrlImpl.class, oid);
		assertEquals("value", "value1", url.getValue());
		assertEquals("type", "type1", url.getType());
		assertEquals("primary", new Boolean(true), url.getPrimary());
		assertEquals("linkText", "linkText1", url.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		url.setValue("value2");
		url.setType("type2");
		url.setPrimary(false);
		url.setLinkText("linkText2");
		session.update(url);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		url = (UrlImpl)session.get(UrlImpl.class, oid);
		assertEquals("value", "value2", url.getValue());
		assertEquals("type", "type2", url.getType());
		assertEquals("primary", new Boolean(false), url.getPrimary());
		assertEquals("linkText", "linkText2", url.getLinkText());
		tx.commit();
		//
		tx = session.beginTransaction();
		url = (UrlImpl)session.get(UrlImpl.class, oid);
		session.delete(url);
		tx.commit();
		tx = session.beginTransaction();
		url = (UrlImpl)session.get(UrlImpl.class, oid);
		assertNull(url);
		tx.commit();
	}

}
