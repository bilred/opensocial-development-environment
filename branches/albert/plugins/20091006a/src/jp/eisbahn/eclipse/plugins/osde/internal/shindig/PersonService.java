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
package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PersonService {
	
	private Session session;

	public PersonService(Session session) {
		super();
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	public List<Person> getPeople() {
		Query query = session.createQuery("select p from PersonImpl p");
		List<?> people = query.list();
		return (List<Person>)people;
	}

	public Person storePerson(Person person) {
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(person);
		tx.commit();
		return person;
	}
	
	public Person createNewPerson(String id, String displayName) {
		Person person = new PersonImpl();
		person.setId(id);
		person.setDisplayName(displayName);
		return storePerson(person);
	}
	
	public void deletePerson(Person person) {
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("select r from RelationshipImpl r where r.person = :person or r.target = :target");
		query.setParameter("person", person);
		query.setParameter("target", person);
		List<RelationshipImpl> relations = (List<RelationshipImpl>)query.list();
		for (RelationshipImpl relation : relations) {
			session.delete(relation);
		}
		session.delete(person);
		tx.commit();
	}
	
	@SuppressWarnings("unchecked")
	public List<RelationshipImpl> getRelationshipList(Person person) {
		Query query = session.createQuery("select r from RelationshipImpl r where r.person = :person");
		query.setParameter("person", person);
		List<?> results = query.list();
		return (List<RelationshipImpl>)results;
	}

	public RelationshipImpl createRelationship(String groupId, Person person, Person target) throws HibernateException {
		Transaction tx = session.beginTransaction();
		RelationshipImpl relationship = new RelationshipImpl();
		relationship.setGroupId(groupId);
		relationship.setPerson(person);
		relationship.setTarget(target);
		session.save(relationship);
		tx.commit();
		return relationship;
	}

	public void deleteRelationship(RelationshipImpl relation) {
		Transaction tx = session.beginTransaction();
		session.delete(relation);
		tx.commit();
	}

	public void removeAll() {
		List<Person> people = getPeople();
		for (Person person : people) {
			deletePerson(person);
		}
	}

}