package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Query;
import org.hibernate.Session;

public class ActivityService {
	
	private Session session;

	public ActivityService(Session session) {
		super();
		this.session = session;
	}

	public List<Activity> getActivities(Person person) {
		Query query = session.createQuery("select a from ActivityImpl a where a.userId = :userId order by a.postedTime desc");
		query.setParameter("userId", person.getId());
		return (List<Activity>)query.list();
	}
	
}
