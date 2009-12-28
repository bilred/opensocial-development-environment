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
import java.io.UnsupportedEncodingException;

/**
 * Represents a Firefox executable.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class FirefoxBinary {
    private String executableLocation;

    FirefoxBinary(String executableLocation) {
        this.executableLocation = executableLocation;
    }

    public void createProfile(String profileName) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                executableLocation, "-no-remote", "-CreateProfile", profileName);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        new OutputWatcher(process).start();
    }

    public void launch(String profileName, String url) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                executableLocation, "-no-remote", "-P", profileName, url);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        new OutputWatcher(process).start();
    }


    private static class OutputWatcher implements Runnable {
        final Process process;
        final ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();

        OutputWatcher(Process process) {
            this.process = process;
        }

        void start() {
            new Thread(this, "OutputWatcher Thread").start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // TODO
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

        String getConsoleOutput() {
            try {
                return consoleOutput.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return consoleOutput.toString();
            }
        }
    }
}
