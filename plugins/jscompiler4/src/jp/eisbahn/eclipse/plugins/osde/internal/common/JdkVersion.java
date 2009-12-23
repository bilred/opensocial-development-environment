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

package jp.eisbahn.eclipse.plugins.osde.internal.common;

/**
 * @author Dolphin Chi-Ngai Wan
 */
public class JdkVersion {

    private static boolean jdk6;

    static {
        try {
            Class.forName("java.util.ArrayDeque");
            jdk6 = true;
        } catch (ClassNotFoundException e) {
            jdk6 = false;
        }
    }

    // utility class not instantiable
    private JdkVersion() {
    }

    public static boolean isAtLeastJdk6() {
        return jdk6;
    }
}
