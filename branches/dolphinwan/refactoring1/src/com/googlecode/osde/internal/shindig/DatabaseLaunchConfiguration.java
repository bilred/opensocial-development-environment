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

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.common.AbstractJob;
import com.googlecode.osde.internal.common.JavaLaunchConfigurationBuilder;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Configuration to launch the Shindig database. Note that all
 * operation are started in another thread and return immediately.
 *
 * @author Dolphin Wan
 */
public class DatabaseLaunchConfiguration {
    private static final String CONFIGURATION_NAME = "Shindig Database";

    public void create() {
        new AbstractJob("Create the database launch configuration") {
            @Override
            protected void runImpl(IProgressMonitor monitor) throws Exception {
                JavaLaunchConfigurationBuilder builder =
                        new JavaLaunchConfigurationBuilder(CONFIGURATION_NAME)
                                .withLibrary("/libs/h2-1.1.117.jar")
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

                builder.removeExistingConfiguration();
                builder.build();
            }
        }.schedule();
    }

    public void delete() {
        new AbstractJob("Delete the Apache Shindig launch configuration") {
            @Override
            protected void runImpl(IProgressMonitor monitor) throws Exception {
                new JavaLaunchConfigurationBuilder(CONFIGURATION_NAME)
                        .removeExistingConfiguration();
            }
        }.schedule();
    }
}
