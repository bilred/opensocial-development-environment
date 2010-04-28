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
 * specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.osde.internal.igoogle;

import com.googlecode.osde.internal.shindig.ShindigServer;

/**
 * This is a convenience utility class that holds common constants.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgConstants {
    static final String LOCAL_HOST_URL =
            "http://localhost:" + ShindigServer.DEFAULT_SHINDIG_PORT + "/";
    static final String GADGET_FILE_WITH_MODIFIED_URL = "modified_gadget.xml";
}
