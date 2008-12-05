package jp.eisbahn.eclipse.plugins.osde.internal.db;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import junit.framework.TestCase;

import org.apache.shindig.social.opensocial.jpa.PersonDb;
import org.apache.shindig.social.opensocial.jpa.hibernate.Bootstrap;
import org.apache.shindig.social.opensocial.model.Person;

public class PersonTest extends TestCase {
	
	private EntityManager em;
	
	public PersonTest() {
		super();
		Bootstrap b = new Bootstrap("org.h2.Driver",
				"jdbc:h2:tcp://localhost:9092/shindig", "sa", "", "1", "1");
		em = b.getEntityManager("hibernate");
	}

	protected void setUp() throws Exception {
		super.setUp();
		deleteAll();
	}
	
	private void deleteAll() throws Exception {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Query query = em.createQuery("delete from PersonDb p");
		query.executeUpdate();
		tx.commit();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreatePerson() throws Exception {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		//
		Person person = new PersonDb();
		person.setId("john.doe");
		person.setAboutMe("aboutMe1");
		// TODO Accounts
		person.setActivities(Arrays.asList("activity1", "activity2", "activity3"));
		person.setAge(33);
		
		//
		em.persist(person);
		tx.commit();
		//
		tx.begin();
		//
		Query query = em.createNamedQuery(PersonDb.FINDBY_PERSONID);
		query.setParameter(PersonDb.PARAM_PERSONID, "john.doe");
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<?> plist = query.getResultList();
		person = null;
		if (plist != null && plist.size() > 0) {
			person = (Person) plist.get(0);
		}
		assertEquals("ID", person.getId(), "john.doe");
		assertEquals("AboutMe", person.getAboutMe(), "aboutMe1");
		// TODO Accounts
		List<String> activities = person.getActivities();
		assertEquals("Activities.length", activities.size(), 3);
		assertEquals("Activities[0]", activities.get(0), "activity1");
		assertEquals("Activities[1]", activities.get(1), "activity2");
		assertEquals("Activities[2]", activities.get(2), "activity3");
		assertEquals("Age", person.getAge(), new Integer(33));
		
		//
		tx.commit();
	}

}
