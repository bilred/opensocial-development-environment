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

package com.googlecode.osde.internal.shindig;

import java.io.IOException;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.common.ExternalApp;
import com.googlecode.osde.internal.common.JavaLaunchConfigurationBuilder;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;

/**
 * Configuration to launch the Shindig database.
 *
 * @author Dolphin Wan
 */
public class DatabaseServer extends ExternalApp {
    private static final String H2_DATABASE_LIBRARY = "/libs/h2-1.1.117.jar";
    private static final Logger logger = new Logger(DatabaseServer.class);

    public DatabaseServer() {
        super("Shindig Database");
    }

    public void createConfiguration() {
        JavaLaunchConfigurationBuilder builder =
                new JavaLaunchConfigurationBuilder(configurationName)
                        .withLibrary(H2_DATABASE_LIBRARY)
                        .withMainClassName("org.h2.tools.Server")
                        .withArgument("-tcp")
                        .withArgument("-tcpAllowOthers");

        OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
        String databaseDir = config.getDatabaseDir();
        if (StringUtils.isNotEmpty(databaseDir)) {
            if (databaseDir.endsWith("\\")) {
                databaseDir = databaseDir.substring(0, databaseDir.length() - 1);
            }
            builder.withArgument("-baseDir");
            builder.withArgumentQuoted(databaseDir);
        }

        try {
            builder.removeExistingConfiguration();
            builder.build();
        } catch (CoreException e) {
            logger.error("Failed to create the database launch configuration", e);
        } catch (IOException e) {
            logger.error("Failed to create the database launch configuration", e);
        }
    }

    public void deleteConfiguration() {
        try {
            new JavaLaunchConfigurationBuilder(configurationName)
                    .removeExistingConfiguration();
        } catch (CoreException e) {
            logger.error("Failed to delete the Apache Shindig launch configuration", e);
        }
    }
}
