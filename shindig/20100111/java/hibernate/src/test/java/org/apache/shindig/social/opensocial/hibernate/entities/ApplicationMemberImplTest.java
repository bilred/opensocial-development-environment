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
package org.apache.shindig.social.opensocial.hibernate.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.Transaction;
import org.junit.Test;

public class ApplicationMemberImplTest extends AbstractEntityTest {

    @Test
    public void testSimple() throws Exception {
        Transaction tx = session.beginTransaction();
        PersonImpl person1 = new PersonImpl();
        person1.setId("person1");
        ApplicationImpl application1 = new ApplicationImpl();
        application1.setId("application1");
        ApplicationMemberImpl applicationMember = new ApplicationMemberImpl();
        applicationMember.setPerson(person1);
        applicationMember.setApplication(application1);
        session.save(applicationMember);
        long oid = applicationMember.getObjectId();
        tx.commit();
        //
        tx = session.beginTransaction();
        applicationMember = (ApplicationMemberImpl)session.get(ApplicationMemberImpl.class, oid);
        assertNotNull(applicationMember);
        Person person = applicationMember.getPerson();
        assertNotNull(person);
        assertEquals("person.id", "person1", person.getId());
        ApplicationImpl application = applicationMember.getApplication();
        assertNotNull(application);
        assertEquals("application.id", "application1", application.getId());
        //
        tx = session.beginTransaction();
        applicationMember = (ApplicationMemberImpl)session.get(ApplicationMemberImpl.class, oid);
        PersonImpl person2 = new PersonImpl();
        person2.setId("person2");
        ApplicationImpl application2 = new ApplicationImpl();
        application2.setId("application2");
        applicationMember.setPerson(person2);
        applicationMember.setApplication(application2);
        session.save(applicationMember);
        tx.commit();
        session.clear();
        tx = session.beginTransaction();
        applicationMember = (ApplicationMemberImpl)session.get(ApplicationMemberImpl.class, oid);
        person = applicationMember.getPerson();
        assertNotNull(person);
        assertEquals("person.id", "person2", person.getId());
        application = applicationMember.getApplication();
        assertNotNull(application);
        assertEquals("application.id", "application2", application.getId());
        //
        tx = session.beginTransaction();
        applicationMember = (ApplicationMemberImpl)session.get(ApplicationMemberImpl.class, oid);
        long poid = ((PersonImpl)applicationMember.getPerson()).getObjectId();
        long aoid = ((ApplicationImpl)applicationMember.getApplication()).getObjectId();
        session.delete(applicationMember);
        tx.commit();
        tx = session.beginTransaction();
        applicationMember = (ApplicationMemberImpl)session.get(ApplicationMemberImpl.class, oid);
        assertNull(applicationMember);
        person = (PersonImpl)session.get(PersonImpl.class, poid);
        assertNotNull(person);
        application = (ApplicationImpl)session.get(ApplicationImpl.class, aoid);
        assertNotNull(application);
    }

}
