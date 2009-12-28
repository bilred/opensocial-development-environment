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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the "profiles.ini" file in a Firefox application data folder.
 * Note that it is a simplified version of WebDriver's Firefox driver.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class ProfilesIni {
    private Map<String, Profile> profiles = new HashMap<String, Profile>();

    public ProfilesIni() {
        File appData = locateAppDataDirectory(Platform.getCurrent());
        profiles = readProfiles(appData);
    }

    private Map<String, Profile> readProfiles(File appData) {
        Map<String, Profile> toReturn = new HashMap<String, Profile>();

        File profilesIni = new File(appData, "profiles.ini");
        if (!profilesIni.exists()) {
            // Fine. No profiles.ini file
            return toReturn;
        }

        boolean isRelative = true;
        String name = null;
        String path = null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(profilesIni));

            String line = reader.readLine();

            while (line != null) {
                if (line.startsWith("[Profile")) {
                    Profile profile = newProfile(name, appData, path, isRelative);
                    if (profile != null) {
                        toReturn.put(name, profile);
                    }

                    name = null;
                    path = null;
                } else if (line.startsWith("Name=")) {
                    name = line.substring("Name=".length());
                } else if (line.startsWith("IsRelative=")) {
                    isRelative = line.endsWith("1");
                } else if (line.startsWith("Path=")) {
                    path = line.substring("Path=".length());
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ProfilingException(e);
        } finally {
            try {
                if (reader != null) {
                    Profile profile = newProfile(name, appData, path, isRelative);
                    if (profile != null) {
                        toReturn.put(name, profile);
                    }

                    reader.close();
                }
            } catch (IOException e) {
                // Nothing that can be done sensibly. Swallowing.
            }
        }

        return toReturn;
    }

    private Profile newProfile(String name, File appData, String path, boolean isRelative) {
        if (name != null && path != null) {
            File profileDir = isRelative ? new File(appData, path) : new File(path);
            return new Profile(profileDir);
        }
        return null;
    }

    /**
     * Returns an in-memory presentation of an existing Firefox profile.
     *
     * @return An profile instance, or null there is such profile name.
     */
    public Profile getProfile(String profileName) {
        Profile profile = profiles.get(profileName);
        if (profile == null) {
            return null;
        }

        return profile;
    }

    private File locateAppDataDirectory(Platform os) {
        File appData;
        switch (os) {
            case WINDOWS:
            case VISTA:
            case XP:
                appData = new File(
                        MessageFormat.format("{0}\\Mozilla\\Firefox", System.getenv("APPDATA")));
                break;

            case MAC:
                appData = new File(
                        MessageFormat.format("{0}/Library/Application Support/Firefox",
                                System.getenv("HOME")));
                break;

            default:
                appData = new File(
                        MessageFormat.format("{0}/.mozilla/firefox", System.getenv("HOME")));
                break;
        }

        if (!appData.exists()) {
            // It's possible we're being run as part of an automated build.
            // Assume the user knows what they're doing
            return null;
        }

        if (!appData.isDirectory()) {
            throw new ProfilingException("The discovered user firefox data directory " +
                    "(which normally contains the profiles) isn't a directory: " +
                    appData.getAbsolutePath());
        }

        return appData;
    }
}

