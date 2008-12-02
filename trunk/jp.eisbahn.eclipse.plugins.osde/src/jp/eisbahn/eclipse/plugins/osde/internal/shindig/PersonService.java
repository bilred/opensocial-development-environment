package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shindig.social.opensocial.model.Person;

public class PersonService {

	private EntityManager em;
	
	public PersonService(EntityManager em) {
		super();
		this.em = em;
	}

	public void close() {
		if (em != null) {
			em.close();
		}
		em = null;
	}

	public List<Person> getPeople() {
		Query query = em.createQuery("select p from PersonDb p");
		List<Person> people = (List<Person>)query.getResultList();
		return people;
	}
	
}
