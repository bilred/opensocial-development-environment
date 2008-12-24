package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

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
	
	public void closeSession() {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
}
