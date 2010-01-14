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

import java.io.File;

/**
 * Performs discovery of firefox instances. This is a simplified version of
 * <code>org.openqa.selenium.firefox.internal.Executable</code>.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class FirefoxLocator {
    private static final File PLATFORM_BINARY = locateFirefoxBinaryFromPlatform();

    private final File binary;

    public FirefoxLocator() {
        if (PLATFORM_BINARY != null && PLATFORM_BINARY.exists()) {
            binary = PLATFORM_BINARY;
        } else {
            binary = null;
        }
    }

    /**
     * Returns the absolute path of an Firefox executable.
     *
     * @return An absolute path, or an empty string if there is no Firefox.
     */
    public String getBinaryLocation() {
        return (binary != null) ? binary.getAbsolutePath() : "";
    }

    private static File locateFirefoxBinaryFromPlatform() {
        File binary = null;

        switch (Platform.getCurrent()) {
            case WINDOWS:
            case VISTA:
            case XP:
                binary = new File(
                        getEnvVar("PROGRAMFILES", "\\Program Files") +
                                "\\Mozilla Firefox\\firefox.exe");
                if (!binary.exists()) {
                    binary = new File("/Program Files (x86)/Mozilla Firefox/firefox.exe");
                }
                break;

            case MAC:
                binary = new File("/Applications/Firefox.app/Contents/MacOS/firefox-bin");
                break;

            default:
                // Do nothing
        }

        return binary != null && binary.exists() ? binary :
                findBinary("firefox3", "firefox2", "firefox");
    }

    private static String getEnvVar(String name, String defaultValue) {
        final String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    /**
     * Walk a PATH to locate binaries with a specified name.
     *
     * @return the first binary found matching that name.
     */
    private static File findBinary(String... binaryNames) {
        final String[] paths = System.getenv("PATH").split(File.pathSeparator);
        final Platform currentPlatform = Platform.getCurrent();
        for (String binaryName : binaryNames) {
            for (String path : paths) {
                File file = new File(path, binaryName);
                if (file.exists() && !file.isDirectory()) {
                    return file;
                }
                if (currentPlatform.is(Platform.WINDOWS)) {
                    File exe = new File(path, binaryName + ".exe");
                    if (exe.exists() && !exe.isDirectory()) {
                        return exe;
                    }
                }
            }
        }
        return null;
    }
}
