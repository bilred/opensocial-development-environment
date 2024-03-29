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
package org.apache.shindig.social.opensocial.oauth;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.sample.oauth.SampleContainerOAuthLookupService;

import com.google.inject.ImplementedBy;

import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

@ImplementedBy(SampleContainerOAuthLookupService.class)

public interface OAuthLookupService {
  boolean thirdPartyHasAccessToUser(OAuthMessage message, String appUrl,
      String userId) throws OAuthException;
  SecurityToken getSecurityToken(String appUrl, String userId);
}
