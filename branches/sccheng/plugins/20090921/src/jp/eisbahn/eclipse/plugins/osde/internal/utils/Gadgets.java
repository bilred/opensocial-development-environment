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
package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Gadgets {

	public static String trim(String source) {
		if (source == null) {
			return "";
		} else {
			return source.trim();
		}
	}
	
	public static String normalize(String source) {
		if (source == null) {
			return null;
		} else if (source.length() == 0) {
			return null;
		} else {
			return source;
		}
	}
	
	public static String string(Integer value) {
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}
	
	public static String string(Float value) {
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}
	
	public static String string(Date value) {
		if (value == null) {
			return "";
		} else {
			return new SimpleDateFormat().format(value);
		}
	}
	
	public static String stringFromTimeMillis(long value) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(value);
		return new SimpleDateFormat().format(cal.getTime());
	}
	
	public static Integer toInteger(String value) {
		if (value == null) {
			return null;
		} else if (value.length() == 0) {
			return null;
		} else {
			try {
				return new Integer(value);
			} catch(NumberFormatException e) {
				return null;
			}
		}
	}

	public static Float toFloat(String value) {
		if (value == null) {
			return null;
		} else if (value.length() == 0) {
			return null;
		} else {
			try {
				return new Float(value);
			} catch(NumberFormatException e) {
				return null;
			}
		}
	}
	
	public static String toHexString(byte[] arr) {
        StringBuffer buff = new StringBuffer(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String b = Integer.toHexString(arr[i] & 0xff);
            if (b.length() == 1) {
                buff.append("0");
            }
            buff.append(b);
        }
        return buff.toString();
    }
	
	public static Date getDate(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, date);
		return cal.getTime();
	}
	
	public static Date getDate(int year, int month, int date, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, date, hour, minute, second);
		return cal.getTime();
	}
	
}
