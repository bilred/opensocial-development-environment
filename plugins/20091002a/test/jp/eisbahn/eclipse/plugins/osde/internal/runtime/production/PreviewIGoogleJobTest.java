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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime.production;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleJobTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link PreviewIGoogleJob#run(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
    public void testRunIProgressMonitor() {
        // TODO: Implement testRunIProgressMonitor()
    }

    /**
     * Test method for {@link PreviewIGoogleJob#uploadFilesToIg()}.
     */
    @Test
    public void testUploadFilesToIg() {
        String username = "osde.test.001";
        String password = "osdetest888";
        IFile gadgetXmlFile = null; // FIXME
        PreviewIGoogleJob job = new PreviewIGoogleJob("Preview iGoogle gadget", username,
                password, false, gadgetXmlFile);
    }

}
