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

public class BodyTypeImplTest extends AbstractEntityTest {
	
//	@Test
//	public void testPerson() throws Exception {
//		Transaction tx = session.beginTransaction();
//		BodyTypeImpl bodyType = createBodyType("build1", "eyeColor1", "hairColor1", 123F, 456F);
//		PersonImpl person = new PersonImpl();
//		person.setAboutMe("aboutMe1");
//		bodyType.setPerson(person);
//		person.setBodyType(bodyType);
//		session.save(bodyType);
//		long oid = bodyType.getObjectId();
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
//		person = (PersonImpl)bodyType.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe1", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
//		person = (PersonImpl)bodyType.getPerson();
//		person.setAboutMe("aboutMe2");
//		session.save(bodyType);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
//		person = (PersonImpl)bodyType.getPerson();
//		assertNotNull(person);
//		assertEquals("aboutMe", "aboutMe2", person.getAboutMe());
//		tx.commit();
//		//
//		tx = session.beginTransaction();
//		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
//		person = (PersonImpl)bodyType.getPerson();
//		bodyType.setPerson(null);
//		person.setBodyType(null);
//		session.save(bodyType);
//		tx.commit();
//		session.clear();
//		tx = session.beginTransaction();
//		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
//		person = (PersonImpl)bodyType.getPerson();
//		assertNull(person);
//		tx.commit();
//	}
	
	private BodyTypeImpl createBodyType(String build, String eyeColor, String hairColor, float height, float weight) {
		BodyTypeImpl bodyType = new BodyTypeImpl();
		bodyType.setBuild(build);
		bodyType.setEyeColor(eyeColor);
		bodyType.setHairColor(hairColor);
		bodyType.setHeight(height);
		bodyType.setWeight(weight);
		return bodyType;
	}
	
	@Test
	public void testSimple() throws Exception {
		Transaction tx = session.beginTransaction();
		BodyTypeImpl bodyType = createBodyType("build1", "eyeColor1", "hairColor1", 123F, 456F);
		session.save(bodyType);
		long oid = bodyType.getObjectId();
		tx.commit();
		session.clear();
		//
		tx = session.beginTransaction();
		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
		assertEquals("build", "build1", bodyType.getBuild());
		assertEquals("eyeColor", "eyeColor1", bodyType.getEyeColor());
		assertEquals("hairColor", "hairColor1", bodyType.getHairColor());
		assertEquals("height", new Float(123F), bodyType.getHeight());
		assertEquals("weight", new Float(456F), bodyType.getWeight());
		tx.commit();
		//
		tx = session.beginTransaction();
		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
		bodyType.setBuild("build2");
		bodyType.setEyeColor("eyeColor2");
		bodyType.setHairColor("hairColor2");
		bodyType.setHeight(321F);
		bodyType.setWeight(654F);
		session.update(bodyType);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
		assertEquals("build", "build2", bodyType.getBuild());
		assertEquals("eyeColor", "eyeColor2", bodyType.getEyeColor());
		assertEquals("hairColor", "hairColor2", bodyType.getHairColor());
		assertEquals("height", new Float(321F), bodyType.getHeight());
		assertEquals("weight", new Float(654F), bodyType.getWeight());
		tx.commit();
		//
		tx = session.beginTransaction();
		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
		session.delete(bodyType);
		tx.commit();
		tx = session.beginTransaction();
		bodyType = (BodyTypeImpl)session.get(BodyTypeImpl.class, oid);
		assertNull(bodyType);
		tx.commit();
	}

}
