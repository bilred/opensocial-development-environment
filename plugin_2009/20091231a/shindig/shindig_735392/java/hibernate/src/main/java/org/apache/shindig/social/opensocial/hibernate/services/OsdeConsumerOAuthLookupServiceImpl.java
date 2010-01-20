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

import java.io.IOException;
import java.net.URISyntaxException;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.SimpleOAuthValidator;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.oauth.OAuthLookupService;
import org.hibernate.Query;
import org.hibernate.Session;

public class OsdeConsumerOAuthLookupServiceImpl implements OAuthLookupService {

	public SecurityToken getSecurityToken(String appUrl, String userId) {
		ApplicationImpl appInfo = getApplication(appUrl);
		return new OAuthSecurityToken(userId, appUrl, appInfo.getId(), "osde");
	}

	public boolean thirdPartyHasAccessToUser(OAuthMessage message, String appUrl, String userId) throws OAuthException {
		ApplicationImpl appInfo = getApplication(appUrl);
		if (appInfo != null) {
			String appId = appInfo.getId();
			return hasValidSignature(message, appUrl, appId, appInfo) && userHasAppInstalled(userId, appId);
		} else {
			return false;
		}
	}

	private boolean userHasAppInstalled(String userId, String appId) {
		// TODO User has app absolutely.
		return true;
	}

	private boolean hasValidSignature(OAuthMessage message, String appUrl, String appId, ApplicationImpl appInfo) throws OAuthException {
		String sharedSecret = appInfo.getConsumerSecret();
		OAuthServiceProvider provider = new OAuthServiceProvider(null, null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, appUrl, sharedSecret, provider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		SimpleOAuthValidator validator = new SimpleOAuthValidator();
		try {
			validator.validateMessage(message, accessor);
		} catch (IOException e) {
			throw new OAuthException(e);
		} catch (URISyntaxException e) {
			throw new OAuthException(e);
		}
		return true;
	}
	
	private ApplicationImpl getApplication(String appUrl) {
		Session session = HibernateUtils.currentSession();
		Query query = session.createQuery("select a from ApplicationImpl a where a.consumerKey = :consumerKey");
		query.setParameter("consumerKey", appUrl);
		ApplicationImpl appInfo = (ApplicationImpl)query.uniqueResult();
		return appInfo;
	}

}
