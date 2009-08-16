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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class) 
@SuiteClasses({
	AccountImplTest.class,
	ActivityImplTest.class,
	MediaItemImplTest.class,
	AddressImplTest.class,
	BodyTypeImplTest.class,
	PersonImplTest.class,
	LookingForImplTest.class,
	EmailImplTest.class,
	ImImplTest.class,
	SmokerImplTest.class,
	DrinkerImplTest.class,
	PhoneNumberImplTest.class,
	PhotoImplTest.class,
	NameImplTest.class,
	OrganizationImplTest.class,
	ApplicationDataMapImplTest.class,
	ApplicationImplTest.class,
	RelationshipImplTest.class,
	UrlImplTest.class,
	UserPrefImplTest.class
})
public class AllTests {
}
