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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="application_member")
public class ApplicationMemberImpl {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="oid")
    protected long objectId;
    
    @Version
    @Column(name="version")
    protected long version;

    @OneToOne(targetEntity=PersonImpl.class, fetch=FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    protected Person person;

    @OneToOne(targetEntity=ApplicationImpl.class, fetch=FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    protected ApplicationImpl application;

    public long getObjectId() {
        return objectId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ApplicationImpl getApplication() {
        return application;
    }

    public void setApplication(ApplicationImpl application) {
        this.application = application;
    }

}
