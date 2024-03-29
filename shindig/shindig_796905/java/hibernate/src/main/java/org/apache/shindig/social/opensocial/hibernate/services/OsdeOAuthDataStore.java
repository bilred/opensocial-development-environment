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

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.AuthenticationMode;
import org.apache.shindig.common.crypto.Crypto;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.apache.shindig.social.sample.spi.JsonDbOpensocialService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

// Sample implementation for OAuth data store
public class OsdeOAuthDataStore implements OAuthDataStore {
    // This needs to be long enough that an attacker can't guess it. If the
    // attacker can guess this
    // value before they exceed the maximum number of attempts, they can
    // complete a session fixation
    // attack against a user.
    private final int CALLBACK_TOKEN_LENGTH = 6;

    // We limit the number of trials before disabling the request token.
    private final int CALLBACK_TOKEN_ATTEMPTS = 5;

    // used to get samplecontainer data from canonicaldb.json
    private final JsonDbOpensocialService service;
    private final OAuthServiceProvider SERVICE_PROVIDER;

    @Inject
    public OsdeOAuthDataStore(JsonDbOpensocialService dbService,
            @Named("shindig.oauth.base-url") String baseUrl) {
        this.service = dbService;
        this.SERVICE_PROVIDER = new OAuthServiceProvider(baseUrl
                + "requestToken", baseUrl + "authorize", baseUrl
                + "accessToken");
    }

    // All valid OAuth tokens
    private static ConcurrentMap<String, OAuthEntry> oauthEntries = new MapMaker()
            .makeMap();

    // Get the OAuthEntry that corresponds to the oauthToken
    public OAuthEntry getEntry(String oauthToken) {
        Preconditions.checkNotNull(oauthToken);
        return oauthEntries.get(oauthToken);
    }

    public OAuthConsumer getConsumer(String consumerKey) {
        ApplicationImpl application = getApplication(consumerKey);
        String consumerSecret = application.getConsumerSecret();
        OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, SERVICE_PROVIDER);
        consumer.setProperty("title", application.getTitle());
        return consumer;
    }

    // Generate a valid requestToken for the given consumerKey
    public OAuthEntry generateRequestToken(String consumerKey,
            String oauthVersion, String signedCallbackUrl) {
        OAuthEntry entry = new OAuthEntry();
        entry.appId = consumerKey;
        entry.consumerKey = consumerKey;
        entry.domain = "samplecontainer.com";
        entry.container = "default";

        entry.token = UUID.randomUUID().toString();
        entry.tokenSecret = UUID.randomUUID().toString();

        entry.type = OAuthEntry.Type.REQUEST;
        entry.issueTime = new Date();
        entry.oauthVersion = oauthVersion;
        if (signedCallbackUrl != null) {
            entry.callbackUrlSigned = true;
            entry.callbackUrl = signedCallbackUrl;
        }

        oauthEntries.put(entry.token, entry);
        return entry;
    }

    // Turns the request token into an access token
    public OAuthEntry convertToAccessToken(OAuthEntry entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(entry.type == OAuthEntry.Type.REQUEST,
                "Token must be a request token");

        OAuthEntry accessEntry = new OAuthEntry(entry);

        accessEntry.token = UUID.randomUUID().toString();
        accessEntry.tokenSecret = UUID.randomUUID().toString();

        accessEntry.type = OAuthEntry.Type.ACCESS;
        accessEntry.issueTime = new Date();

        oauthEntries.remove(entry.token);
        oauthEntries.put(accessEntry.token, accessEntry);

        return accessEntry;
    }

    // Authorize the request token for the given user id
    public void authorizeToken(OAuthEntry entry, String userId) {
        Preconditions.checkNotNull(entry);
        entry.authorized = true;
        entry.userId = Preconditions.checkNotNull(userId);
        if (entry.callbackUrlSigned) {
            entry.callbackToken = Crypto.getRandomDigits(CALLBACK_TOKEN_LENGTH);
        }
    }

    public void disableToken(OAuthEntry entry) {
        Preconditions.checkNotNull(entry);
        ++entry.callbackTokenAttempts;
        if (!entry.callbackUrlSigned
                || entry.callbackTokenAttempts >= CALLBACK_TOKEN_ATTEMPTS) {
            entry.type = OAuthEntry.Type.DISABLED;
        }

        oauthEntries.put(entry.token, entry);
    }

    public void removeToken(OAuthEntry entry) {
        Preconditions.checkNotNull(entry);

        oauthEntries.remove(entry.token);
    }

    // Return the proper security token for a 2 legged oauth request that has
    // been validated
    // for the given consumerKey. App specific checks like making sure the
    // requested user has the
    // app installed should take place in this method
    public SecurityToken getSecurityTokenForConsumerRequest(String consumerKey, String userId) {
        String domain = "osde";
        String container = "default";

        return new OAuthSecurityToken(userId, null, consumerKey, domain,
                container, AuthenticationMode.OAUTH_CONSUMER_REQUEST.name());

    }

    protected ApplicationImpl getApplication(String consumerKey) {
        Session session = HibernateUtils.currentSession();
        Query query = session
                .createQuery("select a from ApplicationImpl a where a.consumerKey = :consumerKey");
        query.setParameter("consumerKey", consumerKey);
        ApplicationImpl appInfo = (ApplicationImpl) query.uniqueResult();
        return appInfo;
    }

}
