/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets;

import org.apache.shindig.common.cache.Cache;
import org.apache.shindig.common.cache.CacheProvider;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.gadgets.http.RequestPipeline;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.LocaleSpec;
import org.apache.shindig.gadgets.spec.MessageBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * Default implementation of a message bundle factory.
 */
@Singleton
public class DefaultMessageBundleFactory extends AbstractSpecFactory<MessageBundle>
    implements MessageBundleFactory {
  private static final Locale ALL_ALL = new Locale("all", "ALL");
  public static final String CACHE_NAME = "messageBundles";

  @Inject
  public DefaultMessageBundleFactory(ExecutorService executor,
                                     RequestPipeline pipeline,
                                     CacheProvider cacheProvider,
                                     @Named("shindig.cache.xml.refreshInterval") long refresh) {
    super(MessageBundle.class, executor, pipeline, makeCache(cacheProvider), refresh);
  }

  private static Cache<Uri, Object> makeCache(CacheProvider cacheProvider) {
    return cacheProvider.createCache(CACHE_NAME);
  }

  @Override
  protected MessageBundle parse(String content, Query query) throws GadgetException {
    return new MessageBundle(((LocaleQuery) query).locale, content);
  }

  public MessageBundle getBundle(GadgetSpec spec, Locale locale, boolean ignoreCache)
      throws GadgetException {
    MessageBundle exact = getBundleFor(spec, locale, ignoreCache);
    MessageBundle lang = getBundleFor(spec, new Locale(locale.getLanguage(), "ALL"), ignoreCache);
    MessageBundle country = getBundleFor(spec, new Locale("all", locale.getCountry()), ignoreCache);
    MessageBundle all = getBundleFor(spec, ALL_ALL, ignoreCache);

    return new MessageBundle(all, country, lang, exact);
  }

  private MessageBundle getBundleFor(GadgetSpec spec, Locale locale, boolean ignoreCache)
      throws GadgetException {
    LocaleSpec localeSpec = spec.getModulePrefs().getLocale(locale);
    if (localeSpec == null) {
      return MessageBundle.EMPTY;
    }

    if (localeSpec.getMessages().toString().length() == 0) {
      return localeSpec.getMessageBundle();
    }

    LocaleQuery query = new LocaleQuery();
    query.setSpecUri(localeSpec.getMessages())
         .setGadgetUri(spec.getUrl())
         // TODO: Get the real container that was used during the request here.
         .setContainer(ContainerConfig.DEFAULT_CONTAINER)
         .setIgnoreCache(ignoreCache);
    query.locale = localeSpec;

    return super.getSpec(query);
  }

  private static class LocaleQuery extends Query {
    // We just use this to hold the locale used in the original query so that parsing can see it.
    LocaleSpec locale;
  }
}
