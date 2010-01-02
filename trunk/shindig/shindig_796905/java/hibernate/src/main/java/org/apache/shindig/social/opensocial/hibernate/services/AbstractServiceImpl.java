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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.hibernate.Query;
import org.hibernate.Session;

public abstract class AbstractServiceImpl {

	protected Set<String> getIdSet(UserId user, GroupId group, SecurityToken token) {
		return getIdSet(user, group, null, token);
	}
	
	@SuppressWarnings("unchecked")
	protected Set<String> getIdSet(
			UserId user, GroupId group, CollectionOptions collectionOptions, SecurityToken token) {
		Session session = HibernateUtils.currentSession();
		Set<String> ids = new TreeSet<String>();
		String userId = user.getUserId(token);
		if (group == null) {
			ids.add(userId);
			return ids;
		}
		String filter = collectionOptions != null ? collectionOptions.getFilter() : null;
		Type type = group.getType();
		if (type.equals(GroupId.Type.all)) {
			Query query;
			if (PersonService.HAS_APP_FILTER.equals(filter)) {
				query = session.createQuery(
						"select r.target.id from RelationshipImpl r join r.target t, ApplicationMemberImpl a "
						+ "where a.person = t and r.person.id = :pid and a.application.id = :aid");
				query.setParameter("pid", userId);
				query.setParameter("aid", token.getAppId());
			} else {
				query = session.createQuery("select r.target.id from RelationshipImpl r where r.person.id = :id");
				query.setParameter("id", userId);
			}
			List<String> resultList = (List<String>)query.list();
			ids.addAll(resultList);
			return ids;
		} else if (type.equals(GroupId.Type.friends)) {
			Query query;
			if (PersonService.HAS_APP_FILTER.equals(filter)) {
				query = session.createQuery(
						"select r.target.id from RelationshipImpl r join r.target t, ApplicationMemberImpl a "
						+ "where a.person = t and r.person.id = :pid and a.application.id = :aid and r.groupId = 'friends'");
				query.setParameter("pid", userId);
				query.setParameter("aid", token.getAppId());
			} else {
				query = session.createQuery("select r.target.id from RelationshipImpl r where r.person.id = :id and r.groupId = 'friends'");
				query.setParameter("id", userId);
			}
			List<String> resultList = (List<String>)query.list();
			ids.addAll(resultList);
			return ids;
		} else if (type.equals(GroupId.Type.groupId)) {
			Query query;
			if (PersonService.HAS_APP_FILTER.equals(filter)) {
				query = session.createQuery(
						"select r.target.id from RelationshipImpl r join r.target t, ApplicationMemberImpl a "
						+ "where a.person = t and r.person.id = :pid and a.application.id = :aid and r.groupId = :groupId");
				query.setParameter("pid", userId);
				query.setParameter("aid", token.getAppId());
				query.setParameter("groupId", group.getGroupId());
			} else {
				query = session.createQuery("select r.target.id from RelationshipImpl r where r.person.id = :id and r.groupId = :groupId");
				query.setParameter("id", userId);
				query.setParameter("groupId", group.getGroupId());
			}
			List<String> resultList = (List<String>)query.list();
			ids.addAll(resultList);
			return ids;
		} else if (type.equals(GroupId.Type.self)) {
			ids.add(userId);
			return ids;
		}
		throw new IllegalArgumentException("No ids which should retrieve.");
	}
	
}
