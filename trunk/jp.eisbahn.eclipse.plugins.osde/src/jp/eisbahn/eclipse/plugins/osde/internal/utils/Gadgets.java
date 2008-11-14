package jp.eisbahn.eclipse.plugins.osde.internal.utils;

public class Gadgets {

	public static String trim(String source) {
		if (source == null) {
			return "";
		} else {
			return source;
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
	
}
