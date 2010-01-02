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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.opensocial.hibernate.entities.PersonImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Query;
import org.hibernate.Session;

public class PersonServiceImpl extends AbstractServiceImpl implements PersonService {

	public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds,
			GroupId groupId, CollectionOptions collectionOptions,
			Set<String> fields, SecurityToken token) throws ProtocolException {
		Set<String> ids = new TreeSet<String>();
		for (UserId userId : userIds) {
			ids.addAll(getIdSet(userId, groupId, collectionOptions, token));
		}
		List<Person> people;
		int startIndex;
		int totalSize;
		if (ids.isEmpty()) {
			people = new ArrayList<Person>();
			startIndex = 0;
			totalSize = 0;
		} else {
			Map<String, Object> resultMap = getPeople(ids, fields, collectionOptions, token);
			people = (List<Person>)resultMap.get("people");
			startIndex = (Integer)resultMap.get("startIndex");
			totalSize = (Integer)resultMap.get("totalSize");
		}
		RestfulCollection<Person> restfulCollection = new RestfulCollection<Person>(people, startIndex, totalSize);
		restfulCollection.setFiltered(false);
		restfulCollection.setSorted(false);
		restfulCollection.setUpdatedSince(false);
		return ImmediateFuture.newInstance(restfulCollection);
	}

	public Future<Person> getPerson(UserId id, Set<String> fields, SecurityToken token) throws ProtocolException {
		Session session = HibernateUtils.currentSession();
		String uid = id.getUserId(token);
		Query query = session.getNamedQuery(PersonImpl.FINDBY_PERSONID);
		query.setParameter(PersonImpl.PARAM_PERSONID, uid);
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<?> people = query.list();
		Person person = null;
		if (people != null && people.size() > 0) {
			person = (Person)people.get(0);
			person.setIsOwner(uid.equals(token.getOwnerId()));
			person.setIsViewer(uid.equals(token.getViewerId()));
		}
		return ImmediateFuture.newInstance(person);
	}
	
	Map<String, Object> getPeople(Set<String> ids, Set<String> fields, CollectionOptions collectionOptions, SecurityToken token) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Session session = HibernateUtils.currentSession();
		int first = collectionOptions.getFirst();
		resultMap.put("startIndex", first);
		int max = collectionOptions.getMax();
		// get totalSize
		Query query = session.createQuery("select count(*) from PersonImpl p where p.id in (:ids)");
		query.setParameterList("ids", ids);
		int totalSize = ((Long)query.uniqueResult()).intValue();
		resultMap.put("totalSize", totalSize);
		// get people
		query = session.createQuery("select p from PersonImpl p where p.id in (:ids) order by p.id");
		query.setParameterList("ids", ids);
		query.setFirstResult(first);
		query.setMaxResults(max);
		List<Person> people = (List<Person>)query.list();
		if (people != null) {
			for (Person person : people) {
				person.setIsOwner(person.getId().equals(token.getOwnerId()));
				person.setIsViewer(person.getId().equals(token.getViewerId()));
			}
		}
		resultMap.put("people", people);
		//
		return resultMap;
	}

}
