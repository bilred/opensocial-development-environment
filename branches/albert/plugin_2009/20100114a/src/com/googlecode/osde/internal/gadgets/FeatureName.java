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
package com.googlecode.osde.internal.gadgets;

public enum FeatureName {

    OPENSOCIAL_0_8("opensocial-0.8"),
    OPENSOCIAL_0_7("opensocial-0.7"),
    PUBSUB("pubsub"),
    VIEWS("views"),
    FLASH("flash"),
    SKINS("skins"),
    DYNAMIC_HEIGHT("dynamic-height"),
    SET_TITLE("settitle"),
    MINI_MESSAGE("minimessage"),
    TABS("tabs");

    private String realName;

    private FeatureName(String realName) {
        this.realName = realName;
    }

    public String toString() {
        return realName;
    }

    public static FeatureName getFeatureName(String realName) {
        for (FeatureName featureName : FeatureName.values()) {
            if (featureName.realName.equals(realName)) {
                return featureName;
            }
        }
        return null;
    }

}
