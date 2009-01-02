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
	
}
