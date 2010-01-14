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
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.ApplicationInformation;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.UserPrefImpl;
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
		String path = appInfo.getPath();
		String consumerKey = appInfo.getConsumerKey();
		String consumerSecret = appInfo.getConsumerSecret();
		Query query = session.createQuery("select a from ApplicationImpl a where a.id = :id");
		query.setParameter("id", appId);
		ApplicationImpl application = (ApplicationImpl)query.uniqueResult();
		if (application == null) {
			application = new ApplicationImpl();
			application.setId(appId);
		}
		application.setTitle(module.getModulePrefs().getTitle());
		application.setPath(path);
		application.setConsumerKey(consumerKey);
		application.setConsumerSecret(consumerSecret);
		session.saveOrUpdate(application);
		tx.commit();
	}
	
	public List<ApplicationImpl> getApplications() {
		Query query = session.createQuery("select a from ApplicationImpl a order by a.id");
		return (List<ApplicationImpl>)query.list();
	}
	
	public ApplicationImpl getApplication(String appId) {
		Query query = session.createQuery("select a from ApplicationImpl a where a.id = :id");
		query.setParameter("id", appId);
		ApplicationImpl application = (ApplicationImpl)query.uniqueResult();
		return application;
	}
	
	public List<UserPrefImpl> getUserPrefs(String appId, String viewerId) {
		Query query = session.createQuery(
				"select u from UserPrefImpl u where u.appId = :appId and u.viewerId = :viewerId");
		query.setParameter("appId", appId);
		query.setParameter("viewerId", viewerId);
		return (List<UserPrefImpl>)query.list();
	}

	public void storeUserPrefs(String appId, String viewerId, Map<String, String> userPrefMap) {
		Transaction tx = session.beginTransaction();
		for (Map.Entry<String, String> entry : userPrefMap.entrySet()) {
			Query query = session.createQuery("select u from UserPrefImpl u where u.appId = :appId and u.viewerId = :viewerId and u.name = :name");
			query.setParameter("appId", appId);
			query.setParameter("viewerId", viewerId);
			query.setParameter("name", entry.getKey());
			UserPrefImpl userPref = (UserPrefImpl)query.uniqueResult();
			if (userPref == null) {
				userPref = new UserPrefImpl();
				userPref.setAppId(appId);
				userPref.setViewerId(viewerId);
				userPref.setName(entry.getKey());
			}
			userPref.setValue(entry.getValue());
			session.saveOrUpdate(userPref);
		}
		tx.commit();
	}

}
