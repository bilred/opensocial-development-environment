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

import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

public class AddressImplTest extends AbstractEntityTest {

//	@Test
//	public void testOrganization() throws Exception {
//		Transaction tx = session.beginTransaction();
//		AddressImpl address = new AddressImpl();
//		OrganizationImpl organization = new OrganizationImpl();
//		organization.setDescription("description1");
//		address.setOrganization(organization);
//		organization.setAddress(address);
//		session.save(address);
//		long oid = address.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		address = (AddressImpl)session.get(AddressImpl.class, oid);
//		organization = (OrganizationImpl)address.getOrganization();
//		assertNotNull(organization);
//		assertEquals("description", "description1", organization.getDescription());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		address = (AddressImpl)session.get(AddressImpl.class, oid);
//		organization = (OrganizationImpl)address.getOrganization();
//		organization.setDescription("description2");
//		session.update(address);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		address = (AddressImpl)session.get(AddressImpl.class, oid);
//		organization = (OrganizationImpl)address.getOrganization();
//		assertEquals("description", "description2", organization.getDescription());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		address = (AddressImpl)session.get(AddressImpl.class, oid);
//		address.setOrganization(null);
//		session.update(address);
//		tx.commit();
//		tx = session.beginTransaction();
//		address = (AddressImpl)session.get(AddressImpl.class, oid);
//		organization = (OrganizationImpl)address.getOrganization();
//		assertNull(organization);
//		tx.commit();
//	}
//
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		AddressImpl name = new AddressImpl();
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		name.setPerson(person);
//		session.save(name);
//		long oid = name.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		name = (AddressImpl)session.get(AddressImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (AddressImpl)session.get(AddressImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(name);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		name = (AddressImpl)session.get(AddressImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (AddressImpl)session.get(AddressImpl.class, oid);
//		name.setPerson(null);
//		session.update(name);
//		tx.commit();
//		tx = session.beginTransaction();
//		name = (AddressImpl)session.get(AddressImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNull(person);
//		tx.commit();
//	}

	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		AddressImpl address = new AddressImpl();
		address.setCountry("country1");
		address.setFormatted("formatted1");
		address.setLatitude(123F);
		address.setLocality("locality1");
		address.setLongitude(456F);
		address.setPostalCode("postalCode1");
		address.setPrimary(true);
		address.setRegion("region1");
		address.setStreetAddress("streetAddress1");
		address.setType("type1");
		session.save(address);
		long oid = address.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		address = (AddressImpl)session.get(AddressImpl.class, oid);
		assertEquals("contry", "country1", address.getCountry());
		assertEquals("formatted", "formatted1", address.getFormatted());
		assertEquals("latitude", new Float(123F), address.getLatitude());
		assertEquals("locality", "locality1", address.getLocality());
		assertEquals("longitude", new Float(456F), address.getLongitude());
		assertEquals("postalCode", "postalCode1", address.getPostalCode());
		assertEquals("primary", new Boolean(true), address.getPrimary());
		assertEquals("region", "region1", address.getRegion());
		assertEquals("streetAddress", "streetAddress1", address.getStreetAddress());
		assertEquals("type", "type1", address.getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		address = (AddressImpl)session.get(AddressImpl.class, oid);
		address.setCountry("country2");
		address.setFormatted("formatted2");
		address.setLatitude(321F);
		address.setLocality("locality2");
		address.setLongitude(654F);
		address.setPostalCode("postalCode2");
		address.setPrimary(false);
		address.setRegion("region2");
		address.setStreetAddress("streetAddress2");
		address.setType("type2");
		session.update(address);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		address = (AddressImpl)session.get(AddressImpl.class, oid);
		assertEquals("contry", "country2", address.getCountry());
		assertEquals("formatted", "formatted2", address.getFormatted());
		assertEquals("latitude", new Float(321F), address.getLatitude());
		assertEquals("locality", "locality2", address.getLocality());
		assertEquals("longitude", new Float(654F), address.getLongitude());
		assertEquals("postalCode", "postalCode2", address.getPostalCode());
		assertEquals("primary", new Boolean(false), address.getPrimary());
		assertEquals("region", "region2", address.getRegion());
		assertEquals("streetAddress", "streetAddress2", address.getStreetAddress());
		assertEquals("type", "type2", address.getType());
		tx.commit();
		//
		tx = session.beginTransaction();
		address = (AddressImpl)session.get(AddressImpl.class, oid);
		session.delete(address);
		tx.commit();
		tx = session.beginTransaction();
		address = (AddressImpl)session.get(AddressImpl.class, oid);
		assertNull(address);
		tx.commit();
	}

}
