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

import com.googlecode.osde.internal.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class StatusUtil {

    public static IStatus newStatus(int severity, String message, Throwable exception) {
        String statusMessage = message;
        if (message == null || message.trim().length() == 0) {
            if (exception == null) {
                throw new IllegalArgumentException();
            } else if (exception.getMessage() == null) {
                statusMessage = exception.toString();
            } else {
                statusMessage = exception.getMessage();
            }
        }
        return new Status(severity, Activator.PLUGIN_ID, severity, statusMessage, exception);
    }

}
