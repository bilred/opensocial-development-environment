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

import org.apache.shindig.social.opensocial.model.Enum.NetworkPresence;
import org.hibernate.Transaction;
import org.junit.Test;

public class NetworkPresenceImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		NetworkPresenceImpl networkPresence = new NetworkPresenceImpl();
//		networkPresence.setDisplayValue("displayValue1");
//		networkPresence.setValue(NetworkPresence.AWAY);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		networkPresence.setPerson(person);
//		session.save(networkPresence);
//		long oid = networkPresence.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
//		person = (PersonImpl)networkPresence.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
//		person = (PersonImpl)networkPresence.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(networkPresence);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
//		person = (PersonImpl)networkPresence.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
//		networkPresence.setPerson(null);
//		session.update(networkPresence);
//		tx.commit();
//		tx = session.beginTransaction();
//		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
//		person = (PersonImpl)networkPresence.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		NetworkPresenceImpl networkPresence = new NetworkPresenceImpl();
		networkPresence.setDisplayValue("displayValue1");
		networkPresence.setValue(NetworkPresence.AWAY);
		session.save(networkPresence);
		long oid = networkPresence.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
		assertEquals("displayValue", "displayValue1", networkPresence.getDisplayValue());
		assertEquals("value", NetworkPresence.AWAY, networkPresence.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
		networkPresence.setDisplayValue("displayValue2");
		networkPresence.setValue(NetworkPresence.CHAT);
		session.update(networkPresence);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
		assertEquals("displayValue", "displayValue2", networkPresence.getDisplayValue());
		assertEquals("value", NetworkPresence.CHAT, networkPresence.getValue());
		tx.commit();
		//
		tx = session.beginTransaction();
		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
		session.delete(networkPresence);
		tx.commit();
		tx = session.beginTransaction();
		networkPresence = (NetworkPresenceImpl)session.get(NetworkPresenceImpl.class, oid);
		assertNull(networkPresence);
		tx.commit();
	}

}
