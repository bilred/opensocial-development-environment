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

import org.apache.shindig.social.opensocial.model.BodyType;

@Entity
@Table(name="body_type")
public class BodyTypeImpl implements BodyType {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="build", length=255)
	protected String build;
	
	@Basic
	@Column(name="eye_color", length=255)
	protected String eyeColor;
	
	@Basic
	@Column(name="hair_color", length=255)
	protected String hairColor;
	
	@Basic
	@Column(name="height")
	protected Float height;
	
	@Basic
	@Column(name="weight")
	protected Float weight;
	
//	@OneToOne(targetEntity=PersonImpl.class, mappedBy="bodyType", fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="person_id", referencedColumnName="oid", insertable=false, updatable=false)
//	protected Person person;
	
	public String getBuild() {
		return build;
	}

	public String getEyeColor() {
		return eyeColor;
	}

	public String getHairColor() {
		return hairColor;
	}

	public Float getHeight() {
		return height;
	}

	public Float getWeight() {
		return weight;
	}
	
//	public Person getPerson() {
//		return person;
//	}
	
	public long getObjectId() {
		return objectId;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setEyeColor(String eyeColor) {
		this.eyeColor = eyeColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}
	
//	public void setPerson(Person person) {
//		this.person = person;
//	}

}
