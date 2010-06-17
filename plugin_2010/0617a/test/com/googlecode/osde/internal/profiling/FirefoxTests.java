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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for profiler.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class FirefoxTests {

    private FirefoxBinary binary;
    private BeaconReceiver server;
    private Map<String, Object> beacon;
    private CountDownLatch beaconReceived;
    private int port;
    private String beaconPath;

    @Before
    public void setUp() throws Exception {
        beaconReceived = new CountDownLatch(1);

        final FirefoxLocator firefoxLocator = new FirefoxLocator();
        String location = firefoxLocator.getBinaryLocation();
        if (location.trim().length() == 0) {
            fail("Cannot locate a Firefox executable");
        }
        binary = new FirefoxBinary(location);

        port = 8900;
        beaconPath = "/beacon/full";
        server = new BeaconReceiver(port, beaconPath);
        server.setListener(new BeaconListener() {
            public void beaconReceived(Map<String, Object> json) {
                beacon = json;
                beaconReceived.countDown();
            }
        });
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testLaunchFirefox() throws IOException, InterruptedException {
        // given
        final String profileName = "profile1";
        Profile profile = binary.createProfile(profileName);
        profile.installPageSpeed("http://localhost:" + port + beaconPath);

        binary.launch(profile, "http://www.google.co.jp");

        beaconReceived.await();

        assertNotNull("Expected beacon does not received", beacon);
    }

}
