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

import java.io.File;
import java.io.IOException;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.common.ExternalApp;
import com.googlecode.osde.internal.common.JavaLaunchConfigurationBuilder;
import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Dolphin Wan
 */
public class ShindigServer extends ExternalApp {
    private static final Logger logger = new Logger(ShindigServer.class);
    private static final String PREBUNDLED_LOGGING_CONFIG_FILE = "/shindig/logging.properties";
    public static final String DEFAULT_SHINDIG_PORT = "8123";

    public ShindigServer() {
        super("Apache Shindig");
    }

    public void createConfiguration() {
        try {
            new JavaLaunchConfigurationBuilder(configurationName)
                    .withLibrary("/libs/jetty-6.1.15.jar")
                    .withLibrary("/libs/jetty-util-6.1.15.jar")
                    .withLibrary("/libs/servlet-api-2.5-6.1.14.jar")
                    .withLibrary("/shindig/juel-api-2.1.2.jar")
                    .withLibrary("/shindig/launcher.jar")
                    .withLibrary("/shindig/slf4j-api-1.5.10.jar")
                    .withLibrary("/shindig/slf4j-log4j12-1.5.10.jar")
                    .withLibrary("/libs/log4j-1.2.14.jar")
                    .withMainClassName("Main")
                    .withArgument(DEFAULT_SHINDIG_PORT)
                    .withArgumentQuoted(Activator.getResourceUrl(
                            "/shindig/shindig-server-1.1-BETA1-incubating.war"))
                    .withArgumentQuoted(
                            Activator.getDefault().getOsdeConfiguration().getJettyDir())
                    .withVmArgument("log4j.configuration", getLoggerConfigurationFile())
                    .withVmArgument("java.util.logging.config.file",
                            getLoggerConfigurationFile())
                    .removeExistingConfiguration()
                    .build();
        } catch (CoreException e) {
            logger.error("Failed to create the Apache Shindig launch configuration", e);
        } catch (IOException e) {
            logger.error("Failed to create the Apache Shindig launch configuration", e);
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

    private String getLoggerConfigurationFile() throws IOException {
        File logFile = new File(Activator.getDefault()
                .getOsdeConfiguration().getLoggerConfigFile());
        if (logFile.isFile() && logFile.exists()) {
            logger.info("Found logger configuration file: " + logFile);
            return logFile.toURI().toURL().toExternalForm();
        }

        // returns the default pre-bundled logging configuration file.
        return Activator.getResourceUrl(PREBUNDLED_LOGGING_CONFIG_FILE);
    }
}
