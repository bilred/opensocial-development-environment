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

package com.googlecode.osde.internal.profiling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.osde.internal.utils.Logger;

/**
 * Represents a Firefox executable.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class FirefoxBinary {
    private static final Logger logger = new Logger(FirefoxBinary.class);

    private String executableLocation;

    FirefoxBinary(String executableLocation) {
        this.executableLocation = executableLocation;
    }

    /**
     * Creates a Firefox profile. No effect if the profile already exists.
     */
    public Profile createProfile(String profileName) throws IOException {
        ProfilesIni ini = new ProfilesIni();
        Profile profile = ini.getProfile(profileName);
        if (profile != null) {
            return profile;
        }

        ProcessBuilder builder = new ProcessBuilder(
                executableLocation, "-no-remote", "-CreateProfile", profileName);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        new ProcessWatcher(process).watch();

        ini = new ProfilesIni();
        return ini.getProfile(profileName);
    }

    /**
     * Launches a Firefox browser window using a given profile.
     */
    public void launch(Profile profile, String url) throws IOException {
        ProcessBuilder builder =
                new ProcessBuilder(executableLocation, "-no-remote", "-P", profile.name, url);

        builder.redirectErrorStream(true);
        Process process = builder.start();

        new ProcessWatcher(process).watch();
    }


    private static class ProcessWatcher implements Runnable {
        final Process process;
        final ByteArrayOutputStream consoleOutput;

        ProcessWatcher(Process process) {
            this.process = process;
            this.consoleOutput = new ByteArrayOutputStream();
        }

        /**
         * This method blocks until the process stops running.
         */
        void watch() {
            new Thread(this, "ProcessWatcher Thread").start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                logger.fine("Firefox process interrupted", e);
            }
        }

        public void run() {
            InputStream stdout = process.getInputStream();

            try {
                int b;
                while ((b = stdout.read()) != -1) {
                    consoleOutput.write(b);
                }
            } catch (IOException e) {
                // nothing we can do.
            }
        }
    }
}
