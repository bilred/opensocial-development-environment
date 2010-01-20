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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationDataMapImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AppDataService {

    private Session session;

    public AppDataService(Session session) {
        super();
        this.session = session;
    }

    public Map<String, String> getApplicationDataMap(Person person, ApplicationImpl application) {
        Query query = session.createQuery(
                "select a from ApplicationDataMapImpl a where a.person = :person and a.application = :application");
        query.setCacheMode(CacheMode.GET);
        query.setParameter("person", person);
        query.setParameter("application", application);
        ApplicationDataMapImpl applicationDataMap = (ApplicationDataMapImpl) query.uniqueResult();
        if (applicationDataMap != null) {
            Map<String, String> result = new HashMap<String, String>();
            result.putAll(applicationDataMap.getDataMap());
            session.evict(applicationDataMap);
            return result;
        } else {
            return null;
        }
    }

    public void removeAll() {
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("select a from ApplicationDataMapImpl a");
        List<ApplicationDataMapImpl> appDatas = (List<ApplicationDataMapImpl>) query.list();
        for (ApplicationDataMapImpl appData : appDatas) {
            session.delete(appData);
        }
        tx.commit();
    }

    public void removeApplicationData(Person person, ApplicationImpl application, String key) {
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(
                "select a from ApplicationDataMapImpl a where a.person = :person and a.application = :application");
        query.setCacheMode(CacheMode.GET);
        query.setParameter("person", person);
        query.setParameter("application", application);
        ApplicationDataMapImpl applicationDataMap = (ApplicationDataMapImpl) query.uniqueResult();
        if (applicationDataMap != null) {
            Map<String, String> dataMap = applicationDataMap.getDataMap();
            dataMap.remove(key);
            session.update(applicationDataMap);
        }
        tx.commit();
    }

    public void addApplicationData(Person person, ApplicationImpl application, String key,
            String value) {
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(
                "select a from ApplicationDataMapImpl a where a.person = :person and a.application = :application");
        query.setCacheMode(CacheMode.GET);
        query.setParameter("person", person);
        query.setParameter("application", application);
        ApplicationDataMapImpl applicationDataMap = (ApplicationDataMapImpl) query.uniqueResult();
        if (applicationDataMap == null) {
            applicationDataMap = new ApplicationDataMapImpl();
            applicationDataMap.setPerson(person);
            applicationDataMap.setApplication(application);
        }
        Map<String, String> dataMap = applicationDataMap.getDataMap();
        dataMap.put(key, value);
        session.saveOrUpdate(applicationDataMap);
        tx.commit();
    }

}
