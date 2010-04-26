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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.ListField;

@Entity
@Table(name="phone_number")
public class PhoneNumberImpl implements ListField {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="field_type", length=255)
	protected String type;

	@Basic
	@Column(name="field_value", length=255)
	protected String value;
	
	@Basic
	@Column(name="primary_flag")
	protected Boolean primary;
	
//	@ManyToOne(targetEntity=PersonImpl.class, fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="person_id", referencedColumnName="oid")
//	protected Person person;

	public long getObjectId() {
		return objectId;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

//	public void setPerson(Person person) {
//		this.person = person;
//	}
//
//	public Person getPerson() {
//		return person;
//	}

}
