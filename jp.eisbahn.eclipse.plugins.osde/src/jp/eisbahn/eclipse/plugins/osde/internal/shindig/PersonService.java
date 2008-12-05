package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.apache.shindig.social.opensocial.jpa.PersonDb;
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

	public void save(Person person) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(person);
		tx.commit();
	}
	
	public Person getJohnDoe() {
		em.clear();
	    String uid = "john.doe";
	    Query q = em.createNamedQuery(PersonDb.FINDBY_PERSONID);
	    q.setParameter(PersonDb.PARAM_PERSONID, uid);
	    q.setFirstResult(0);
	    q.setMaxResults(1);
	    List<?> plist = q.getResultList();
	    Person person = null;
	    if (plist != null && plist.size() > 0) {
	      person = (Person) plist.get(0);
	    }
	    return person;
	}
	
}
