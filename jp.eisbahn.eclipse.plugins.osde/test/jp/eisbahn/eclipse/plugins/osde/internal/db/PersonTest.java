package jp.eisbahn.eclipse.plugins.osde.internal.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.shindig.social.core.model.EnumImpl;
import org.apache.shindig.social.opensocial.jpa.PersonDb;
import org.apache.shindig.social.opensocial.jpa.eclipselink.Bootstrap;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Enum;
import org.apache.shindig.social.opensocial.model.Enum.Drinker;
import org.apache.shindig.social.opensocial.model.Enum.LookingFor;
import org.apache.shindig.social.opensocial.model.Enum.Smoker;
import org.apache.shindig.social.opensocial.model.Person.Gender;

import junit.framework.TestCase;

public class PersonTest extends TestCase {
	
	private EntityManager em;
	
	public PersonTest() {
		super();
		Bootstrap b = new Bootstrap("org.apache.derby.jdbc.ClientDriver",
				"jdbc:derby://localhost:1527/testdb;create=true", "sa", " ", "1", "1");
		em = b.getEntityManager("default");
	}

	protected void setUp() throws Exception {
		super.setUp();
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
//		person.setDrinker(new EnumImpl<Drinker>(Drinker.HEAVILY)); // TODO required?
//		person.setGender(Gender.male); // TODO required?
//		person.setSmoker(new EnumImpl<Smoker>(Smoker.SOCIALLY)); // TODO required?
//		List<Enum<LookingFor>> lookingFor = new ArrayList<Enum<LookingFor>>();
//		lookingFor.add(new EnumImpl<LookingFor>(LookingFor.ACTIVITY_PARTNERS));
//		lookingFor.add(new EnumImpl<LookingFor>(LookingFor.DATING));
//		person.setLookingFor(lookingFor); // TODO required?
		//
		em.persist(person);
		tx.commit();
	}

}
