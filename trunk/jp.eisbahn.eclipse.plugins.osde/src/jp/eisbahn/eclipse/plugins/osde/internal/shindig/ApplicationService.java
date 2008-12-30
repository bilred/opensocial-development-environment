package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.google.gadgets.Module;

public class ApplicationService {
	
	private Session session;

	public ApplicationService(Session session) {
		super();
		this.session = session;
	}

	public void storeAppInfo(ApplicationInformation appInfo) {
		Transaction tx = session.beginTransaction();
		String appId = appInfo.getAppId();
		Module module = appInfo.getModule();
		Query query = session.createQuery("select a from ApplicationImpl a where a.id = :id");
		query.setParameter("id", appId);
		ApplicationImpl application = (ApplicationImpl)query.uniqueResult();
		if (application == null) {
			application = new ApplicationImpl();
			application.setId(appId);
		}
		application.setTitle(module.getModulePrefs().getTitle());
		session.saveOrUpdate(application);
		tx.commit();
	}
	
	public List<ApplicationImpl> getApplications() {
		Query query = session.createQuery("select a from ApplicationImpl a order by a.id");
		return (List<ApplicationImpl>)query.list();
	}

}
