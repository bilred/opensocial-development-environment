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
import java.util.Map;

import com.googlecode.osde.internal.utils.ApplicationInformation;

import com.googlecode.osde.internal.gadgets.model.Module;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationMemberImpl;
import org.apache.shindig.social.opensocial.hibernate.entities.UserPrefImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        ApplicationImpl application = (ApplicationImpl) query.uniqueResult();
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

        @SuppressWarnings("unchecked")
        List<ApplicationImpl> result = (List<ApplicationImpl>) query.list();
        return result;
    }

    public ApplicationImpl getApplication(String appId) {
        Query query = session.createQuery("select a from ApplicationImpl a where a.id = :id");
        query.setParameter("id", appId);

        ApplicationImpl result = (ApplicationImpl) query.uniqueResult();
        return result;
    }

    public List<UserPrefImpl> getUserPrefs(String appId, String viewerId) {
        Query query = session.createQuery(
                "select u from UserPrefImpl u where u.appId = :appId and u.viewerId = :viewerId");
        query.setParameter("appId", appId);
        query.setParameter("viewerId", viewerId);

        @SuppressWarnings("unchecked")
        List<UserPrefImpl> result = (List<UserPrefImpl>) query.list();
        return result;
    }

    public void storeUserPrefs(String appId, String viewerId, Map<String, String> userPrefMap) {
        Transaction tx = session.beginTransaction();
        for (Map.Entry<String, String> entry : userPrefMap.entrySet()) {
            Query query = session.createQuery(
                    "select u from UserPrefImpl u where u.appId = :appId and u.viewerId = :viewerId and u.name = :name");
            query.setParameter("appId", appId);
            query.setParameter("viewerId", viewerId);
            query.setParameter("name", entry.getKey());
            UserPrefImpl userPref = (UserPrefImpl) query.uniqueResult();
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

    public void removeAll() {
        List<ApplicationImpl> applications = getApplications();
        for (ApplicationImpl application : applications) {
            deleteApplication(application);
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteApplication(ApplicationImpl application) {
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(
                "select a from ApplicationMemberImpl a where a.application = :application");
        query.setParameter("application", application);
        List<ApplicationMemberImpl> applicationMembers = (List<ApplicationMemberImpl>)query.list();
        for (ApplicationMemberImpl applicationMember : applicationMembers) {
            session.delete(applicationMember);
        }
        session.delete(application);
        tx.commit();
    }

    public void updateHasApp(ApplicationImpl application, Person person, boolean hasApp) {
        Transaction tx = session.beginTransaction();
        if (hasApp) {
            ApplicationMemberImpl applicationMember = new ApplicationMemberImpl();
            applicationMember.setPerson(person);
            applicationMember.setApplication(application);
            session.save(applicationMember);
        } else {
            Query query = session.createQuery(
                    "from ApplicationMemberImpl a where a.person = :person and a.application = :application");
            query.setParameter("person", person);
            query.setParameter("application", application);
            ApplicationMemberImpl applicationMember = (ApplicationMemberImpl)query.uniqueResult();
            session.delete(applicationMember);
        }
        tx.commit();
    }
    
}
