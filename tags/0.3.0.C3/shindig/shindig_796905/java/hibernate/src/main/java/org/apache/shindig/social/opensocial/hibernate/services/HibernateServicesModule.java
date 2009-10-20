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
package org.apache.shindig.social.opensocial.hibernate.services;

//import org.apache.shindig.social.opensocial.oauth.OAuthLookupService;
import org.apache.shindig.social.core.config.SocialApiGuiceModule;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.sample.oauth.SampleOAuthDataStore;

import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class HibernateServicesModule extends SocialApiGuiceModule {

	@Override
	protected void configure() {
		super.configure();
		//
		bind(PersonService.class).to(PersonServiceImpl.class).in(Scopes.SINGLETON);
		bind(AppDataService.class).to(AppDataServiceImpl.class).in(Scopes.SINGLETON);
		bind(ActivityService.class).to(ActivityServiceImpl.class).in(Scopes.SINGLETON);
		//
		//		bind(OAuthLookupService.class).to(OsdeConsumerOAuthLookupServiceImpl.class).in(Scopes.SINGLETON);
		bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db"))
			.toInstance("sampledata/canonicaldb.json");
		bind(OAuthDataStore.class).to(OsdeOAuthDataStore.class);
	}

}
