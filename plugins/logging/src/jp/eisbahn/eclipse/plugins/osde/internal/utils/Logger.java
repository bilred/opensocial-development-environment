package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.util.logging.Level;

/**
 * A wrapper class around a JUL Logger to remove verboseness.
 * 
 * @author Dolphin Chi-Ngai Wan
 */
public class Logger {
    private final java.util.logging.Logger delegate;

    public Logger(Class<?> owner) {
        delegate = java.util.logging.Logger.getLogger(owner.getCanonicalName());
    }

    public void info(String message) {
        delegate.info(message);
    }

    public void warn(String message) {
        delegate.warning(message);
    }

    public void warn(String message, Throwable cause) {
        delegate.log(Level.WARNING, message, cause);
    }

    public void error(String message) {
        delegate.severe(message);
    }

    public void error(String message, Throwable cause) {
        delegate.log(Level.SEVERE, message, cause);
    }
}
