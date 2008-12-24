package jp.eisbahn.eclipse.plugins.osde.internal.utils;

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
	
}
