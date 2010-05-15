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
package org.apache.shindig.social.opensocial.hibernate.utils;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtils {
	
	private static Log logger = LogFactory.getLog(HibernateUtils.class);
	
	private static SessionFactory sessionFactory;
	
	private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();
	
	public static final String configFileName = "osde_hibernate.cfg.xml";
	
	public static final String configFileDir = System.getProperty("java.io.tmpdir");
	
	private HibernateUtils() {
	}
	
	public static void initialize() throws HibernateException {
		init(new File(configFileDir, configFileName));
	}
	
	public static void initialize(String resource) throws HibernateException {
		init(resource);
	}
	
	private static void init(String resource) throws HibernateException {
		try {
			if (resource != null && !resource.equals("")) {
				sessionFactory = new AnnotationConfiguration().configure(resource).buildSessionFactory();
			} else {
				sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			}
		} catch(Exception e) {
			throw new HibernateException("Can't build hibernate sessionactory.", e);
		}
	}

	private static void init(File configFile) throws HibernateException {
		try {
			if (configFile != null) {
				sessionFactory = new AnnotationConfiguration().configure(configFile).buildSessionFactory();
			} else {
				sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			}
		} catch(Exception e) {
			throw new HibernateException("Can't build hibernate sessionactory.", e);
		}
	}

	public static Session currentSession() throws HibernateException {
		if (sessionFactory == null) {
			initialize();
		}
		Session session = (Session)sessionThreadLocal.get();
		if (session == null) {
			logger.debug("Open Hibernate Session.");
			session = sessionFactory.openSession();
			sessionThreadLocal.set(session);
		}
		return session;
	}
	
	public static void closeSession() throws HibernateException {
		if (sessionFactory == null || sessionThreadLocal == null) {
			return;
		}
		Session session = sessionThreadLocal.get();
		sessionThreadLocal.set(null);
		if (session != null) {
			logger.debug("Close Hibernate Session.");
			session.close();
		}
	}

}
