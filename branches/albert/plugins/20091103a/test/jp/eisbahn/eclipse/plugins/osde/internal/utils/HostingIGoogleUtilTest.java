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
package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.util.List;
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.IgPrefEditToken;

import org.junit.Test;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.*;
import static org.junit.Assert.*;

/**
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Annotate test as large.
public class HostingIGoogleUtilTest {

    private static Logger logger = Logger.getLogger(HostingIGoogleUtil.class.getName());

    // TODO: Get a better place to store test data.
    private static final String TEST_DATA_PATH =
            "test/jp/eisbahn/eclipse/plugins/osde/internal/runtime/production/testdata/";

    /**
     * Test method for {@link HostingIGoogleUtil#uploadFile(
     * String, String, IgPrefEditToken, String, String)} etc.
     *
     * @throws HostingException
     */
    @Test
    public void testAuthenticationAndUploadAndRetrieveFiles()
            throws HostingException {
        // Prepare Authentication.
        String emailUserName = "osde.test.001";
        String password = "osdetest888";
        String sid = retrieveSid(emailUserName, password, null, null);
        logger.info("sid: " + sid);
        assertTrue(sid.length() > 50);
        String publicId = retrievePublicId(sid);
        logger.info("publicId: " + publicId);
        assertTrue(publicId.length() > 20);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);
        assertTrue("Invalid prefEditToken: " + prefEditToken, prefEditToken.validate());

        // Upload file.
        String rootPath = TEST_DATA_PATH;
        HostingIGoogleUtil.uploadFiles(sid, publicId, prefEditToken, rootPath);

        // Retrieve directory info.
        String quotaByte = retrieveQuotaByte(sid, publicId);
        logger.info("quotaByte: " + quotaByte);
        String quotaByteUsed = retrieveQuotaByteUsed(sid, publicId);
        logger.info("quotaByteUsed: " + quotaByteUsed);
        assertTrue(Integer.valueOf(quotaByteUsed) > 100); // dummy_gadget.xml size > 100
        String fileList = retrieveFileList(sid, publicId);
        logger.info("fileList:\n" + fileList);
        assertTrue(fileList.length() > 50);
        String relativeFilePath = "gadget.xml";
        String fileContent = retrieveFile(sid, publicId, relativeFilePath);
        logger.info("fileContent:\n" + fileContent);
        assertTrue(fileContent.startsWith("<?xml version"));
        String previewUrl = formPreviewGadgetUrl(publicId, relativeFilePath, false);
        logger.info("previewUrl: " + previewUrl);
        assertTrue(previewUrl.endsWith(relativeFilePath));
    }

    /**
     * Test method for {@link HostingIGoogleUtil#findAllRelativeFilePaths(String)}.
     */
    @Test
    public final void testFindAllRelativeFilePaths() {
        List<String> filePaths = findAllRelativeFilePaths(TEST_DATA_PATH);

        // Verify the 6 testing files are found.
        assertEquals(6, filePaths.size());
        for (String filePath : filePaths) {
            logger.info("filePath: " + filePath);
        }
    }
}
