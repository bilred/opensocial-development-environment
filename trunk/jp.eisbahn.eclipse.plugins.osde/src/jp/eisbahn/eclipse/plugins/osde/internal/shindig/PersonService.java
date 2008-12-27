package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PersonService {
	
	private Session session;

	public PersonService(Session session) {
		super();
		this.session = session;
	}

	public void closeSession() {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Person> getPeople() {
		Query query = session.createQuery("select p from PersonImpl p");
		List<?> people = query.list();
		return (List<Person>)people;
	}

	public Person store(Person person) {
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(person);
		tx.commit();
		return person;
	}
	
	public Person createNewPerson(String id, String displayName) {
		Person person = new PersonImpl();
		person.setId(id);
		person.setDisplayName(displayName);
		return store(person);
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

	public RelationshipImpl createRelationship(String groupId, Person person, Person target) {
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

}
