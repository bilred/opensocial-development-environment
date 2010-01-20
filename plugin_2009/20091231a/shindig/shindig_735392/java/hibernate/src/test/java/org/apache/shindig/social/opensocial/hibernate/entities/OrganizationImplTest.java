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

import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrganizationImplTest extends AbstractEntityTest {

	@Test
	public void testAddress() throws Exception {
		Transaction tx = session.beginTransaction();
		OrganizationImpl name = new OrganizationImpl();
		AddressImpl address = new AddressImpl();
		address.setCountry("country1");
		name.setAddress(address);
		session.save(name);
		long oid = name.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		address = (AddressImpl)name.getAddress();
		assertNotNull(address);
		assertEquals("country", "country1", address.getCountry());
		tx.commit();
		//
		tx = session.beginTransaction();
		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		address = (AddressImpl)name.getAddress();
		address.setCountry("country2");
		session.update(name);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		address = (AddressImpl)name.getAddress();
		assertEquals("country", "country2", address.getCountry());
		tx.commit();
		//
		tx = session.beginTransaction();
		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		name.setAddress(null);
		session.update(name);
		tx.commit();
		tx = session.beginTransaction();
		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		address = (AddressImpl)name.getAddress();
		assertNull(address);
		tx.commit();
	}

//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		OrganizationImpl name = new OrganizationImpl();
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		name.setPerson(person);
//		session.save(name);
//		long oid = name.getObjectId();
//		tx.commit();
//		session.clear();
//		//
//		tx = session.beginTransaction();
//		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.update(name);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
//		name.setPerson(null);
//		session.update(name);
//		tx.commit();
//		tx = session.beginTransaction();
//		name = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
//		person = (PersonImpl)name.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		OrganizationImpl organization = new OrganizationImpl();
		organization.setDescription("description1");
		Date endDate1 = new Date();
		organization.setEndDate(endDate1);
		organization.setField("field1");
		organization.setName("name1");
		organization.setPrimary(true);
		organization.setSalary("salary1");
		Date startDate1 = new Date();
		organization.setStartDate(startDate1);
		organization.setSubField("subField1");
		organization.setTitle("title1");
		organization.setType("type1");
		organization.setWebpage("webpage1");
		session.save(organization);
		long oid = organization.getObjectId();
		tx.commit();
		//
		tx = session.beginTransaction();
		organization = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		assertEquals("description", "description1", organization.getDescription());
		assertDate("endDate", endDate1, organization.getEndDate());
		assertEquals("field", "field1", organization.getField());
		assertEquals("name", "name1", organization.getName());
		assertEquals("primary", new Boolean(true), organization.getPrimary());
		assertEquals("salary", "salary1", organization.getSalary());
		assertDate("startDate", startDate1, organization.getStartDate());
		assertEquals("subField", "subField1", organization.getSubField());
		assertEquals("title", "title1", organization.getTitle());
		assertEquals("type", "type1", organization.getType());
		assertEquals("webpage", "webpage1", organization.getWebpage());
		tx.commit();
		//
		tx = session.beginTransaction();
		organization = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		organization.setDescription("description2");
		Date endDate2 = new Date();
		organization.setEndDate(endDate2);
		organization.setField("field2");
		organization.setName("name2");
		organization.setPrimary(false);
		organization.setSalary("salary2");
		Date startDate2 = new Date();
		organization.setStartDate(startDate2);
		organization.setSubField("subField2");
		organization.setTitle("title2");
		organization.setType("type2");
		organization.setWebpage("webpage2");
		session.update(organization);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		organization = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		assertEquals("description", "description2", organization.getDescription());
		assertDate("endDate", endDate2, organization.getEndDate());
		assertEquals("field", "field2", organization.getField());
		assertEquals("name", "name2", organization.getName());
		assertEquals("primary", new Boolean(false), organization.getPrimary());
		assertEquals("salary", "salary2", organization.getSalary());
		assertDate("startDate", startDate2, organization.getStartDate());
		assertEquals("subField", "subField2", organization.getSubField());
		assertEquals("title", "title2", organization.getTitle());
		assertEquals("type", "type2", organization.getType());
		assertEquals("webpage", "webpage2", organization.getWebpage());
		tx.commit();
		//
		tx = session.beginTransaction();
		organization = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		session.delete(organization);
		tx.commit();
		tx = session.beginTransaction();
		organization = (OrganizationImpl)session.get(OrganizationImpl.class, oid);
		assertNull(organization);
		tx.commit();
	}

}
