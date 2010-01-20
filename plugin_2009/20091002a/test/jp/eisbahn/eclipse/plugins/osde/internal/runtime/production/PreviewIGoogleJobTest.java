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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleJobTest {

    private static Logger logger = Logger.getLogger(PreviewIGoogleJobTest.class.getName());

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
     * @throws CoreException
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Test
    public void testUploadFilesToIg() throws ClientProtocolException, IOException, CoreException {
        String username = "osde.test.001";
        String password = "osdetest888";
        String path = "test/jp/eisbahn/eclipse/plugins/osde/internal/runtime/"
                + "production/testdata/gadget.xml";
        File gadgetXmlFile = new File(path);
        logger.info("gadgetXmlFile: " + gadgetXmlFile);
        logger.info("gadgetXmlFile abs path: " + gadgetXmlFile.getAbsolutePath());
        logger.info("gadgetXmlFile total space: " + gadgetXmlFile.getTotalSpace());
        logger.info("gadgetXmlFile exists: " + gadgetXmlFile.exists());
        IPath ipath = Path.fromPortableString(path);
        logger.info("ipath: " + ipath);
        logger.info("ipath ext: " + ipath.getFileExtension());

        IFile gadgetXmlIFile = ResourcesPlugin.getWorkspace().getRoot().getFile(ipath);
        //IFile gadgetXmlIFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(ipath);
        logger.info("gadgetXmlIFile: " + gadgetXmlIFile);
        logger.info("gadgetXmlIFile name: " + gadgetXmlIFile.getName());
        logger.info("gadgetXmlIFile raw loc: " + gadgetXmlIFile.getRawLocation()); // FIXME
        PreviewIGoogleJob job = new PreviewIGoogleJob("Preview iGoogle gadget", username,
                password, false, gadgetXmlIFile);
        String result = job.uploadFilesToIg();
        logger.info("result: " + result);
    }

}
