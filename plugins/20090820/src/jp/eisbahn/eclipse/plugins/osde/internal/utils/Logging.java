package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import jp.eisbahn.eclipse.plugins.osde.internal.*;

/**
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 * 
 * Wrapper class for logging information in OSDE plugin.
 * This class wraps logging functions and provides interface for using them.
 * As you would typically do with classic java.util.logging utility, the usage
 * of this logging utility is simple.
 * 
 * First, import his package:
 *     <code>import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logging;</code>
 * 
 * Then you have the following use cases:
 * 1. For CoreException, you can log plugin info as usual:
 * 
 *     <code>try {</code>
 *         <code>...</code>
 *     <code>} catch (CoreException ce) {</code>
 *         <code>Logging.log(ce.getStatus());</code>
 *     <code>}</code>
 * 2. Or more generally:
 * 
 *     <code>try {</code>
 *         <code>...</code>
 *     <code>} catch (Exception e) {</code>
 *         <code>Logging.error("error messages here", e);</code>
 *         or without exception being logged: <code>Logging.error("error messages here");</code>
 *     <code>}</code>
 * 
 * 3. For general information, warnings, and errors, use:
 *     <code>Logging.info("information here");</code>
 *     <code>Logging.warn("warning message here");</code>
 *     <code>Logging.error("error message here")</code>
 * 
 */
public class Logging {
	
	/**
	 * Get the plugin's ILog interface and actually write out logs.
	 * 
	 * @param status Status object that carries information for logging
	 */
	public static void log(IStatus status) {
		Activator.getDefault().getLog().log(status);
	}
	
	public static void info(String message) {
		log(IStatus.INFO, message, null);
	}
	
	public static void warn(String message) {
		log(IStatus.WARNING, message, null);
	}
	
	public static void error(String message) {
		log(IStatus.ERROR, message, null);
	}
	
	public static void error(String message, Throwable exception) {
		log(IStatus.ERROR, message, exception);
	}
	
	public static void log(int severity, String message, Throwable exception) {
		log(new Status(severity, Activator.PLUGIN_ID, message, exception));
	}
	
	/**
	 * No need to get an instance of Logging since it is a wrapper class
	 */
	private Logging(){
	}
}