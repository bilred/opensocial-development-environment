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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.googlecode.osde.internal.Activator;

/**
 * Redirects all log messages from java.util.logging framework to Eclipse
 * Runtime Log Framework. This allows both RCP-dependent/independent code to use
 * the same logging framework (JUL). RCP-independent code, when running outside
 * Eclipse (e.g. unit test), will log to the system console. RCP-dependent code,
 * when running inside Eclipse, will log to the "Error Log" view.
 * 
 * @author Dolphin Chi-Ngai Wan
 */
public class EclipseLogHandler extends Handler {

    @Override
    public void close() throws SecurityException {
        // noop as we have no resource to free.
    }

    @Override
    public void flush() {
        // noop as we don't buffer our log records.
    }

    @Override
    public void publish(LogRecord record) {
        IStatus status = convert(record);
        Activator.getDefault().getLog().log(status);
    }

    /**
     * Converts a JUL log record into an equivalent RCP status.
     */
    private IStatus convert(LogRecord record) {
        return new Status(convert(record.getLevel()), Activator.PLUGIN_ID, record.getMessage(),
                record.getThrown());
    }

    /**
     * Converts a JUL log level into its equivalent RCP log severity level.
     */
    private int convert(Level level) {
        // Note: Since JUL allows API users to define their own log levels, I
        // use a range check instead of an identity check to the Level constants.

        // INFO, CONFIG, FINE, FINER, FINEST
        if (level.intValue() <= Level.CONFIG.intValue()) {
            return IStatus.INFO;
        }

        // WARNING
        if (level.intValue() <= Level.WARNING.intValue()) {
            return IStatus.WARNING;
        }

        // SEVERE
        return IStatus.ERROR;
    }
}
