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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.shindig.social.opensocial.model.Address;
import org.apache.shindig.social.opensocial.model.Organization;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name="organization")
public class OrganizationImpl implements Organization {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="description", length=255)
	protected String description;
	
	@Basic
	@Column(name="end_date")
	@Temporal(TemporalType.DATE)
	protected Date endDate;
	
	@Basic
	@Column(name="field", length=255)
	protected String field;
	
	@Basic
	@Column(name="organization_name", length=255)
	protected String name;
	
	@Basic
	@Column(name="organization_primary")
	protected Boolean primary;
	
	@Basic
	@Column(name="salary", length=255)
	protected String salary;
	
	@Basic
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	protected Date startDate;
	
	@Basic
	@Column(name="sub_field", length=255)
	protected String subField;
	
	@Basic
	@Column(name="title", length=255)
	protected String title;
	
	@Basic
	@Column(name="type", length=255)
	protected String type;
	
	@Basic
	@Column(name="webpage", length=255)
	protected String webpage;
	
	@OneToOne(targetEntity=AddressImpl.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected Address address;
	
//	@ManyToOne(targetEntity=PersonImpl.class, fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="person_id", referencedColumnName="oid")
//	protected Person person;
	
	public long getObjectId() {
		return objectId;
	}
	
	public Address getAddress() {
		return address;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getField() {
		return field;
	}

	public String getName() {
		return name;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public String getSalary() {
		return salary;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getSubField() {
		return subField;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getWebpage() {
		return webpage;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setSubField(String subField) {
		this.subField = subField;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWebpage(String webpage) {
		this.webpage = webpage;
	}

//	public void setPerson(Person person) {
//		this.person = person;
//	}
//
//	public Person getPerson() {
//		return person;
//	}

}
