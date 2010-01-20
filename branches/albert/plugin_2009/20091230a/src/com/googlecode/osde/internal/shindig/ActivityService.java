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
package com.googlecode.osde.internal.shindig;

import java.util.List;

import org.apache.shindig.social.opensocial.hibernate.entities.ActivityImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

	public void removeAll() {
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("select a from ActivityImpl a");
		List<ActivityImpl> activities = (List<ActivityImpl>)query.list();
		for (ActivityImpl activity : activities) {
			session.delete(activity);
		}
		tx.commit();
	}
	
}
