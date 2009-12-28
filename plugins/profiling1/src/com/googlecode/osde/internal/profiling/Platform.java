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

/**
 * Used to extract information such as program locations and line endings.
 * Note that it is a simplified version of WebDriver's common Platform class.
 */
enum Platform {
    /**
     * Any version of Windows.
     */
    WINDOWS("") {
        public boolean is(Platform compareWith) {
            return compareWith == WINDOWS || compareWith == XP || compareWith == VISTA;
        }
    },

    /**
     * For versions of Windows that store files in "\Program Files\" and
     * documents under "\\documents and settings\\username".
     */
    XP("xp", "windows") {
        public boolean is(Platform compareWith) {
            return compareWith == WINDOWS || compareWith == XP;
        }
    },

    /**
     * For versions of Windows that store files under "\\Users\\username".
     */
    VISTA("windows vista", "Windows Server 2008") {
        public boolean is(Platform compareWith) {
            return compareWith == WINDOWS || compareWith == VISTA;
        }
    },

    MAC("mac", "darwin") {},
    UNIX("solaris", "bsd") {},
    LINUX("linux") {
        public boolean is(Platform compareWith) {
            return compareWith == UNIX || compareWith == LINUX;
        }
    };

    private final String[] partOfOsName;

    private Platform(String... partOfOsName) {
        this.partOfOsName = partOfOsName;
    }

    /**
     * Returns the current detected platform.
     */
    static Platform getCurrent() {
        return extractFromSysProperty(System.getProperty("os.name"));
    }

    private static Platform extractFromSysProperty(String osName) {
        osName = osName.toLowerCase();
        Platform mostLikely = UNIX;
        String previousMatch = null;
        for (Platform os : Platform.values()) {
            for (String matcher : os.partOfOsName) {
                if ("".equals(matcher)) {
                    continue;
                }

                if (os.isExactMatch(osName, matcher)) {
                    return os;
                }
                if (os.isCurrentPlatform(osName, matcher) &&
                        isBetterMatch(previousMatch, matcher)) {
                    previousMatch = matcher;
                    mostLikely = os;
                }
            }
        }

        // Default to assuming we're on a unix variant (including linux)
        return mostLikely;
    }

    private static boolean isBetterMatch(String previous, String matcher) {
        return previous == null || matcher.length() >= previous.length();
    }

    public boolean is(Platform compareWith) {
        return this.equals(compareWith);
    }

    private boolean isCurrentPlatform(String osName, String matchAgainst) {
        return osName.indexOf(matchAgainst) != -1;
    }

    private boolean isExactMatch(String osName, String matchAgainst) {
        return matchAgainst.equals(osName);
    }
}
