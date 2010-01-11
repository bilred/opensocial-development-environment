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
package org.apache.shindig.social.opensocial.hibernate.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.BasicSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationMemberImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersonServiceImplTest extends AbstractServiceTest {
	
	private PersonServiceImpl target;
	
	@Before
	public void setupTarget() {
		target = new PersonServiceImpl();
	}
	
	@After
	public void teardownTarget() {
		target = null;
	}
	
	@Test
	public void testGetPerson() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setAboutMe("aboutMe1");
		person.setId("id1");
		session.save(person);
		tx.commit();
		session.clear();
		//
		SecurityToken token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		UserId id = new UserId(UserId.Type.owner, "id1");
		Future<Person> result = target.getPerson(id, null, token);
		Person actual = result.get();
		assertEquals("person.id", "id1", actual.getId());
		assertEquals("person.aboutMe", "aboutMe1", actual.getAboutMe());
	}
	
	@Test
	public void testIsViewerOwner() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person = new PersonImpl();
		person.setId("id1");
		session.save(person);
		tx.commit();
		session.clear();
		//
		SecurityToken token = new BasicSecurityToken("id1", "id1", null, null, null, null, null, null);
		UserId id = new UserId(UserId.Type.userId, "id1");
		Future<Person> result = target.getPerson(id, null, token);
		Person actual = result.get();
		assertEquals("person.id", "id1", actual.getId());
		assertTrue(actual.getIsOwner());
		assertTrue(actual.getIsViewer());
		//
		token = new BasicSecurityToken("id2", "id2", null, null, null, null, null, null);
		id = new UserId(UserId.Type.userId, "id1");
		result = target.getPerson(id, null, token);
		actual = result.get();
		assertEquals("person.id", "id1", actual.getId());
		assertFalse(actual.getIsOwner());
		assertFalse(actual.getIsViewer());
	}

	@Test
	public void testGetIdSet() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		PersonImpl person2 = new PersonImpl();
		person2.setId("id2");
		session.save(person2);
		PersonImpl person3 = new PersonImpl();
		person3.setId("id3");
		session.save(person3);
		RelationshipImpl relation1 = new RelationshipImpl();
		relation1.setGroupId("friends");
		relation1.setPerson(person1);
		relation1.setTarget(person2);
		session.save(relation1);
		RelationshipImpl relation2 = new RelationshipImpl();
		relation2.setGroupId("business");
		relation2.setPerson(person1);
		relation2.setTarget(person3);
		session.save(relation2);
		tx.commit();
		session.clear();
		//
		SecurityToken token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		UserId user = new UserId(UserId.Type.owner, "id1");
		Set<String> actual = target.getIdSet(user, null, token);
		assertEquals("actual.size", 1, actual.size());
		Object[] actualArray = actual.toArray();
		assertEquals("actualArray[0]", "id1", actualArray[0]);
		//
		token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		user = new UserId(UserId.Type.owner, "id1");
		GroupId group = new GroupId(GroupId.Type.all, null);
		actual = target.getIdSet(user, group, token);
		assertEquals("actual.size", 2, actual.size());
		actualArray = actual.toArray();
		assertEquals("actualArray[0]", "id2", actualArray[0]);
		assertEquals("actualArray[1]", "id3", actualArray[1]);
		//
		token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		user = new UserId(UserId.Type.owner, "id1");
		group = new GroupId(GroupId.Type.friends, null);
		actual = target.getIdSet(user, group, token);
		assertEquals("actual.size", 1, actual.size());
		actualArray = actual.toArray();
		assertEquals("actualArray[0]", "id2", actualArray[0]);
		//
		token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		user = new UserId(UserId.Type.owner, "id1");
		group = new GroupId(GroupId.Type.groupId, "business");
		actual = target.getIdSet(user, group, token);
		assertEquals("actual.size", 1, actual.size());
		actualArray = actual.toArray();
		assertEquals("actualArray[0]", "id3", actualArray[0]);
		//
		token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		user = new UserId(UserId.Type.owner, "id1");
		group = new GroupId(GroupId.Type.self, null);
		actual = target.getIdSet(user, group, token);
		assertEquals("actual.size", 1, actual.size());
		actualArray = actual.toArray();
		assertEquals("actualArray[0]", "id1", actualArray[0]);
	}
	
	@Test
	public void testGetPeople() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		PersonImpl person2 = new PersonImpl();
		person2.setId("id2");
		session.save(person2);
		PersonImpl person3 = new PersonImpl();
		person3.setId("id3");
		session.save(person3);
		RelationshipImpl relation1 = new RelationshipImpl();
		relation1.setGroupId("friends");
		relation1.setPerson(person1);
		relation1.setTarget(person2);
		session.save(relation1);
		RelationshipImpl relation2 = new RelationshipImpl();
		relation2.setGroupId("business");
		relation2.setPerson(person1);
		relation2.setTarget(person3);
		session.save(relation2);
		RelationshipImpl relation3 = new RelationshipImpl();
		relation3.setGroupId("friends");
		relation3.setPerson(person2);
		relation3.setTarget(person1);
		session.save(relation3);
		tx.commit();
		session.clear();
		//
		SecurityToken token = new BasicSecurityToken("id1", null, null, null, null, null, null, null);
		UserId user1 = new UserId(UserId.Type.userId, "id1");
		Set<UserId> userIdSet = new HashSet<UserId>();
		userIdSet.add(user1);
		GroupId group = new GroupId(GroupId.Type.all, null);
		CollectionOptions collectionOptions = new CollectionOptions();
		collectionOptions.setFirst(0);
		collectionOptions.setMax(20);
		Future<RestfulCollection<Person>> result = target.getPeople(userIdSet, group, collectionOptions, null, token);
		RestfulCollection<Person> actual = result.get();
		List<Person> people = actual.getEntry();
		assertEquals("people.size", 2, people.size());
		assertEquals("totalResults", 2, actual.getTotalResults());
		assertEquals("people[0].id", "id2", people.get(0).getId());
		assertEquals("people[1].id", "id3", people.get(1).getId());
	}
	
	@Test
	public void testGetPeopleHasApp() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		session.save(person1);
		PersonImpl person2 = new PersonImpl();
		person2.setId("id2");
		session.save(person2);
		PersonImpl person3 = new PersonImpl();
		person3.setId("id3");
		session.save(person3);
		RelationshipImpl relation1 = new RelationshipImpl();
		relation1.setGroupId("friends");
		relation1.setPerson(person1);
		relation1.setTarget(person2);
		session.save(relation1);
		RelationshipImpl relation2 = new RelationshipImpl();
		relation2.setGroupId("business");
		relation2.setPerson(person1);
		relation2.setTarget(person3);
		session.save(relation2);
		RelationshipImpl relation3 = new RelationshipImpl();
		relation3.setGroupId("friends");
		relation3.setPerson(person2);
		relation3.setTarget(person1);
		session.save(relation3);
		tx.commit();
		session.clear();
		//
		SecurityToken token = new BasicSecurityToken("id1", null, "id1", null, null, null, null, null);
		UserId user1 = new UserId(UserId.Type.userId, "id1");
		Set<UserId> userIdSet = new HashSet<UserId>();
		userIdSet.add(user1);
		GroupId group = new GroupId(GroupId.Type.all, null);
		CollectionOptions collectionOptions = new CollectionOptions();
		collectionOptions.setFirst(0);
		collectionOptions.setMax(20);
		Future<RestfulCollection<Person>> result = target.getPeople(userIdSet, group, collectionOptions, null, token);
		RestfulCollection<Person> actual = result.get();
		List<Person> people = actual.getEntry();
		assertEquals("people.size", 2, people.size());
		assertEquals("totalResults", 2, actual.getTotalResults());
		assertEquals("people[0].id", "id2", people.get(0).getId());
		assertEquals("people[1].id", "id3", people.get(1).getId());
		//
		tx = session.beginTransaction();
		person3 = (PersonImpl)session.merge(person3);
		ApplicationImpl application = new ApplicationImpl();
		application.setId("id1");
		ApplicationMemberImpl applicationMember = new ApplicationMemberImpl();
		applicationMember.setPerson(person3);
		applicationMember.setApplication(application);
		session.save(applicationMember);
		tx.commit();
		//
		collectionOptions.setFilter(PersonService.HAS_APP_FILTER);
		result = target.getPeople(userIdSet, group, collectionOptions, null, token);
		actual = result.get();
		people = actual.getEntry();
		assertEquals("people.size", 1, people.size());
		assertEquals("totalResults", 1, actual.getTotalResults());
		assertEquals("people[0].id", "id3", people.get(0).getId());
	}

}
