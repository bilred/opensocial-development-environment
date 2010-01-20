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

import org.apache.shindig.social.opensocial.model.Name;

@Entity
@Table(name="person_name")
public class NameImpl implements Name {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="additional_name", length=255)
	protected String additionalName;
	
	@Basic
	@Column(name="family_name", length=255)
	protected String familyName;
	
	@Basic
	@Column(name="given_name", length=255)
	protected String givenName;
	
	@Basic
	@Column(name="honorific_prefix", length=255)
	protected String honorificPrefix;
	
	@Basic
	@Column(name="honorific_suffix", length=255)
	protected String honorificSuffix;
	
	@Basic
	@Column(name="unstructured", length=255)
	protected String unstructured;
	
//	@ManyToOne(targetEntity=PersonImpl.class, fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="person_id", referencedColumnName="oid")
//	protected Person person;

	public long getObjectId() {
		return objectId;
	}

	public String getAdditionalName() {
		return additionalName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	public String getHonorificSuffix() {
		return honorificSuffix;
	}

	public String getUnstructured() {
		return unstructured;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}

	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;
	}

	public void setUnstructured(String unstructured) {
		this.unstructured = unstructured;
	}

//	public void setPerson(Person person) {
//		this.person = person;
//	}
//
//	public Person getPerson() {
//		return person;
//	}

}
