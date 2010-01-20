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

import org.apache.shindig.social.opensocial.model.Address;

@Entity
@Table(name="address")
public class AddressImpl implements Address {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="oid")
	protected long objectId;
	
	@Version
	@Column(name="version")
	protected long version;
	
	@Basic
	@Column(name="country", length=255)
	protected String country;
	
	@Basic
	@Column(name="latitude")
	protected Float latitude;
	
	@Basic
	@Column(name="longitude")
	protected Float longitude;
	
	@Basic
	@Column(name="locality", length=255)
	protected String locality;
	
	@Basic
	@Column(name="postal_code", length=255)
	protected String postalCode;
	
	@Basic
	@Column(name="region", length=255)
	protected String region;
	
	@Basic
	@Column(name="street_address", length=255)
	protected String streetAddress;
	
	@Basic
	@Column(name="type", length=255)
	protected String type;
	
	@Basic
	@Column(name="formatted", length=255)
	protected String formatted;
	
	@Basic
	@Column(name="primary_address")
	private Boolean primary;
	
//	@ManyToOne(targetEntity=PersonImpl.class, fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="person_id", referencedColumnName="oid")
//	protected Person person;
	
//	@OneToOne(targetEntity=OrganizationImpl.class, mappedBy="address", fetch=FetchType.LAZY)
//	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
//	@JoinColumn(name="organization_id", referencedColumnName="oid", insertable=false, updatable=false)
//	protected Organization organization;
	
	public String getCountry() {
		return country;
	}

	public String getFormatted() {
		return formatted;
	}

	public Float getLatitude() {
		return latitude;
	}

	public String getLocality() {
		return locality;
	}

	public Float getLongitude() {
		return longitude;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public String getRegion() {
		return region;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public String getType() {
		return type;
	}
	
	public long getObjectId() {
		return objectId;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public void setType(String type) {
		this.type = type;
	}

//	public void setPerson(Person person) {
//		this.person = person;
//	}
//
//	public Person getPerson() {
//		return person;
//	}
//
//	public void setOrganization(Organization organization) {
//		this.organization = organization;
//	}
//
//	public Organization getOrganization() {
//		return organization;
//	}

}
