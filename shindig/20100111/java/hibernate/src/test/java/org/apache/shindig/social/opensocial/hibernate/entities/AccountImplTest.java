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

public class AccountImplTest extends AbstractEntityTest {

//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		AccountImpl account = new AccountImpl();
//		account.setDomain("domain1");
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		account.setPerson(person);
//		session.save(account);
//		long oid = account.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		account = (AccountImpl)session.get(AccountImpl.class, oid);
//		person = (PersonImpl)account.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		account = (AccountImpl)session.get(AccountImpl.class, oid);
//		person = (PersonImpl)account.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(account);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		account = (AccountImpl)session.get(AccountImpl.class, oid);
//		person = (PersonImpl)account.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		account = (AccountImpl)session.get(AccountImpl.class, oid);
//		account.setPerson(null);
//		session.update(account);
//		tx.commit();
//		tx = session.beginTransaction();
//		account = (AccountImpl)session.get(AccountImpl.class, oid);
//		person = (PersonImpl)account.getPerson();
//		assertNull(person);
//		tx.commit();
//	}

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		AccountImpl account = new AccountImpl();
		account.setDomain("domain1");
		account.setUserId("userId1");
		account.setUsername("username1");
		session.save(account);
		tx.commit();
		long oid = account.getObjectId();
		//
		session.clear();
		tx = session.beginTransaction();
		account = (AccountImpl)session.get(AccountImpl.class, oid);
		assertEquals("domain", "domain1", account.getDomain());
		assertEquals("userId", "userId1", account.getUserId());
		assertEquals("username", "username1", account.getUsername());
		tx.commit();
		//
		tx = session.beginTransaction();
		account = (AccountImpl)session.get(AccountImpl.class, oid);
		account.setDomain("domain2");
		account.setUserId("userId2");
		account.setUsername("username2");
		session.update(account);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		account = (AccountImpl)session.get(AccountImpl.class, oid);
		tx.commit();
		assertEquals("domain", "domain2", account.getDomain());
		assertEquals("userId", "userId2", account.getUserId());
		assertEquals("username", "username2", account.getUsername());
		//
		tx = session.beginTransaction();
		account = (AccountImpl)session.get(AccountImpl.class, oid);
		session.delete(account);
		tx.commit();
		tx = session.beginTransaction();
		account = (AccountImpl)session.get(AccountImpl.class, oid);
		assertNull(account);
		tx.commit();
	}

}
