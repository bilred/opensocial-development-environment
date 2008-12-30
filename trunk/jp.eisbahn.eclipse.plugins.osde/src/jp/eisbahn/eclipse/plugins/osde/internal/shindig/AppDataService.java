package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.HashMap;
import java.util.Map;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationDataMapImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;

public class AppDataService {
	
	private Session session;

	public AppDataService(Session session) {
		super();
		this.session = session;
	}
	
	public Map<String, String> getApplicationDataMap(Person person, ApplicationImpl application) {
		Query query = session.createQuery("select a from ApplicationDataMapImpl a where a.person = :person and a.application = :application");
		query.setCacheMode(CacheMode.GET);
		query.setParameter("person", person);
		query.setParameter("application", application);
		ApplicationDataMapImpl applicationDataMap = (ApplicationDataMapImpl)query.uniqueResult();
		if (applicationDataMap != null) {
			Map<String, String> result = new HashMap<String, String>();
			result.putAll(applicationDataMap.getDataMap());
			session.evict(applicationDataMap);
			return result;
		} else {
			return null;
		}
	}

}
