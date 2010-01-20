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
package com.googlecode.osde.internal.utils;

import java.util.logging.Level;

/**
 * A wrapper class around a JUL Logger to remove verboseness. This class
 * enforces proper usage of JUL e.g. use class.getCanonicalName() instead of
 * class.getName().
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class Logger {
    // We can safely keep a Logger instance here as LogManager also keeps
    // loggers forever.
    private final java.util.logging.Logger delegate;

    public Logger(Class<?> owner) {
        delegate = java.util.logging.Logger.getLogger(owner.getCanonicalName());
    }

    public void fine(String message) {
        if (delegate.isLoggable(Level.FINE)) {
            delegate.fine(message);
        }
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
