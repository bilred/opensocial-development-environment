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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractServiceTest {

	protected Session session;
	
	@Before
	public void createSession() throws Exception {
		HibernateUtils.initialize("hibernate_for_test.cfg.xml");
		session = HibernateUtils.currentSession();
	}
	
	@After
	public void closeSession() throws Exception {
		HibernateUtils.closeSession();
		session = null;
	}
	
	protected void assertTimestamp(String message, Date expected, Date actual) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(expected);
		int expectedYear = cal.get(Calendar.YEAR);
		int expectedMonth = cal.get(Calendar.MONTH);
		int expectedDate = cal.get(Calendar.DATE);
		int expectedHour = cal.get(Calendar.HOUR_OF_DAY);
		int expectedMinute = cal.get(Calendar.MINUTE);
		int expectedSecond = cal.get(Calendar.SECOND);
		cal.setTime(actual);
		int actualYear = cal.get(Calendar.YEAR);
		int actualMonth = cal.get(Calendar.MONTH);
		int actualDate = cal.get(Calendar.DATE);
		int actualHour = cal.get(Calendar.HOUR_OF_DAY);
		int actualMinute = cal.get(Calendar.MINUTE);
		int actualSecond = cal.get(Calendar.SECOND);
		assertEquals(message + "-year", expectedYear, actualYear);
		assertEquals(message + "-month", expectedMonth, actualMonth);
		assertEquals(message + "-date", expectedDate, actualDate);
		assertEquals(message + "-hour", expectedHour, actualHour);
		assertEquals(message + "-minute", expectedMinute, actualMinute);
		assertEquals(message + "-second", expectedSecond, actualSecond);
	}
	
}
