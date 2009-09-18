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
package jp.eisbahn.eclipse.plugins.osde.internal.db;

import java.util.List;

import junit.framework.TestCase;

import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PersonTest extends TestCase {
	
	private Session session;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		HibernateUtils.initialize("hibernate_for_test.cfg.xml");
		session = HibernateUtils.currentSession();
		deleteAll();
	}
	
	private void deleteAll() throws Exception {
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("delete from PersonImpl p");
		query.executeUpdate();
		tx.commit();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtils.closeSession();
	}
	
	public void testCreatePerson() throws Exception {
		Transaction tx = session.beginTransaction();
		Person person = new PersonImpl();
		person.setId("john.doe");
		person.setAboutMe("aboutMe1");
		person.setAge(33);
		session.save(person);
		tx.commit();
		session.clear();

		tx = session.beginTransaction();
		Query query = session.getNamedQuery(PersonImpl.FINDBY_PERSONID);
		query.setParameter(PersonImpl.PARAM_PERSONID, "john.doe");
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<?> plist = query.list();
		person = null;
		if (plist != null && plist.size() > 0) {
			person = (Person)plist.get(0);
		}
		assertEquals("id", "john.doe", person.getId());
		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
		assertEquals("Age", new Integer(33), person.getAge());
		tx.commit();
	}

}
