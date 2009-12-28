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

/**
 * @author Dolphin Chi-Ngai Wan
 */
public class FirefoxTests {

    private FirefoxBinary binary;
    private BeaconReceiver server;
    private Map beacon;
    private CountDownLatch beaconReceived;
    private int port;

    @Before
    public void setUp() throws Exception {
        beaconReceived = new CountDownLatch(1);

        binary = new FirefoxLocator(null).getBinary();

        port = 8900;
        server = new BeaconReceiver(port);
        server.setListener(new BeaconListener() {
            public void beaconReceived(Map json) {
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
        final String profileName = "abc2";
        binary.createProfile(profileName);

        ProfilesIni ini = new ProfilesIni();
        Profile profile = ini.getProfile(profileName);
        profile.installPageSpeed("http://localhost:" + port + "/beacon/full");

        binary.launch(profileName, "http://www.google.co.jp");

        beaconReceived.await();

        assertNotNull("Expected beacon does not received", beacon);
    }

}
