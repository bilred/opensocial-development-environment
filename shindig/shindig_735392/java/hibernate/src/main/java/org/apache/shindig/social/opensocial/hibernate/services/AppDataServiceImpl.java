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
package org.apache.shindig.social.opensocial.hibernate.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.ResponseError;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationDataMapImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.DataCollection;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AppDataServiceImpl extends AbstractServiceImpl implements AppDataService {

	public Future<Void> deletePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws SocialSpiException {
		Type type = groupId.getType();
		if (type.equals(Type.self)) {
			if (userId.getUserId(token).equals(token.getViewerId())) {
				if (fields.contains("*")) {
					deleteAppData(userId.getUserId(token), null, appId);
				} else {
					for (String key : fields) {
						deleteAppData(userId.getUserId(token), key, appId);
					}
				}
				return ImmediateFuture.newInstance(null);
			} else {
				throw new SocialSpiException(ResponseError.FORBIDDEN, "The data of the user who is not VIEWER cannot be removed. ");
			}
		} else {
			throw new SocialSpiException(ResponseError.NOT_IMPLEMENTED, "We don't support updating data in batches yet.");
		}
	}

	public Future<DataCollection> getPersonData(Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws SocialSpiException {
		Set<String> ids = new TreeSet<String>();
		for (UserId userId : userIds) {
			ids.addAll(getIdSet(userId, groupId, token));
		}
		Session session = HibernateUtils.currentSession();
		Query query = session.createQuery("select p from PersonImpl p where p.id in (:ids) order by p.id");
		query.setParameterList("ids", ids);
		List<Person> people = (List<Person>)query.list();
		query = session.createQuery("select d from ApplicationDataMapImpl d where d.person in (:people) order by d.person.id");
		query.setParameterList("people", people);
		List<ApplicationDataMapImpl> dataMapList = (List<ApplicationDataMapImpl>)query.list();
		Map<String, Map<String, String>> entry = new HashMap<String, Map<String,String>>();
		for (ApplicationDataMapImpl appData : dataMapList) {
			String personId = appData.getPerson().getId();
			Map<String, String> dataMap = appData.getDataMap();
			dataMap = filterMap(dataMap, fields);
			if (dataMap.size() > 0) {
				entry.put(personId, dataMap);
			}
		}
		return ImmediateFuture.newInstance(new DataCollection(entry));
	}
	
	private Map<String, String> filterMap(Map<String, String> map, Set<String> fields) {
		if (fields.contains("*") || fields.isEmpty()) {
			return map;
		} else {
			Map<String, String> resultMap = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				if (fields.contains(key)) {
					resultMap.put(key, entry.getValue());
				}
			}
			return resultMap;
		}
	}

	public Future<Void> updatePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, Map<String, String> values, SecurityToken token) throws SocialSpiException {
		Type type = groupId.getType();
		if (type.equals(Type.self)) {
			if (userId.getUserId(token).equals(token.getViewerId())) {
				for (String key : fields) {
					String value = values.get(key);
					setAppData(userId.getUserId(token), key, value, appId);
				}
				return ImmediateFuture.newInstance(null);
			} else {
				throw new SocialSpiException(ResponseError.FORBIDDEN, "The data of the user who is not VIEWER cannot be updated.");
			}
		} else {
			throw new SocialSpiException(ResponseError.NOT_IMPLEMENTED, "We don't support updating data in batches yet.");
		}
	}

	void setAppData(String userId, String key, String value, String appId) {
		Session session = HibernateUtils.currentSession();
		Transaction tx = session.beginTransaction();
		ApplicationDataMapImpl applicationDataMap = getApplicationDataMap(session, userId, appId);
		Map<String, String> dataMap = applicationDataMap.getDataMap();
		if (StringUtils.isEmpty(value)) {
			dataMap.remove(key);
		} else {
			dataMap.put(key, value);
		}
		session.saveOrUpdate(applicationDataMap);
		tx.commit();
	}

	void deleteAppData(String userId, String key, String appId) {
		Session session = HibernateUtils.currentSession();
		Transaction tx = session.beginTransaction();
		ApplicationDataMapImpl applicationDataMap = getApplicationDataMap(session, userId, appId);
		Map<String, String> dataMap = applicationDataMap.getDataMap();
		if (key != null) {
			dataMap.remove(key);
		} else {
			dataMap.clear();
		}
		session.saveOrUpdate(applicationDataMap);
		tx.commit();
	}
	
	ApplicationDataMapImpl getApplicationDataMap(Session session, String userId, String appId) {
		Query query = session.createQuery("select p from PersonImpl p where p.id = :id");
		query.setParameter("id", userId);
		Person person = (Person)query.uniqueResult();
		query = session.createQuery("select a from ApplicationImpl a where a.id = :id");
		query.setParameter("id", appId);
		ApplicationImpl application = (ApplicationImpl)query.uniqueResult();
		query = session.createQuery("select a from ApplicationDataMapImpl a where a.person = :person and a.application = :application");
		query.setParameter("person", person);
		query.setParameter("application", application);
		ApplicationDataMapImpl dataMap = (ApplicationDataMapImpl)query.uniqueResult();
		if (dataMap == null) {
			dataMap = new ApplicationDataMapImpl();
			dataMap.setPerson(person);
			dataMap.setApplication(application);
		}
		return dataMap;
	}

}
