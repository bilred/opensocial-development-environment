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

public class RelationshipImplTest extends AbstractEntityTest {
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		PersonImpl person1 = new PersonImpl();
		person1.setId("id1");
		PersonImpl person2 = new PersonImpl();
		person2.setId("id2");
		RelationshipImpl relation = new RelationshipImpl();
		relation.setPerson(person1);
		relation.setTarget(person2);
		relation.setGroupId("groupId1");
		session.save(relation);
		long oid = relation.getObjectId();
		tx.commit();
		//
		tx = session.beginTransaction();
		relation = (RelationshipImpl)session.get(RelationshipImpl.class, oid);
		assertNotNull(relation);
		assertEquals("groupId", "groupId1", relation.getGroupId());
		assertEquals("person.id", "id1", relation.getPerson().getId());
		assertEquals("target.id", "id2", relation.getTarget().getId());
		tx.commit();
		//
		tx = session.beginTransaction();
		relation = (RelationshipImpl)session.get(RelationshipImpl.class, oid);
		relation.setGroupId("groupId2");
		PersonImpl person3 = new PersonImpl();
		person3.setId("id3");
		PersonImpl person4 = new PersonImpl();
		person4.setId("id4");
		relation.setPerson(person3);
		relation.setTarget(person4);
		session.save(relation);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		relation = (RelationshipImpl)session.get(RelationshipImpl.class, oid);
		assertEquals("groupId", "groupId2", relation.getGroupId());
		assertEquals("person.id", "id3", relation.getPerson().getId());
		assertEquals("target.id", "id4", relation.getTarget().getId());
		tx.commit();
		//
		tx = session.beginTransaction();
		relation = (RelationshipImpl)session.get(RelationshipImpl.class, oid);
		long poid1 = ((PersonImpl)relation.getPerson()).getObjectId();
		long poid2 = ((PersonImpl)relation.getTarget()).getObjectId();
		session.delete(relation);
		tx.commit();
		tx = session.beginTransaction();
		relation = (RelationshipImpl)session.get(RelationshipImpl.class, oid);
		assertNull(relation);
		person1 = (PersonImpl)session.get(PersonImpl.class, poid1);
		assertNotNull(person1);
		person2 = (PersonImpl)session.get(PersonImpl.class, poid2);
		assertNotNull(person2);
		tx.commit();
	}

}
