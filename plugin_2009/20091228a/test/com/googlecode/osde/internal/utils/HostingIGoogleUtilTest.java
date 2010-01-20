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
package com.googlecode.osde.internal.utils;

import java.util.List;
import java.util.logging.Logger;

import com.googlecode.osde.internal.utils.IgCredentials;

import org.junit.Test;

import static com.googlecode.osde.internal.utils.HostingIGoogleUtil.*;
import static org.junit.Assert.*;

/**
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Annotate test as large.
public class HostingIGoogleUtilTest {

    private static final String DUMMY_HOST_FOLDER = "/dummy_host_folder/";

    private static Logger logger = Logger.getLogger(HostingIGoogleUtil.class.getName());

    // TODO: Get a better place to store test data.
    private static final String TEST_DATA_PATH =
            "test/jp/eisbahn/eclipse/plugins/osde/internal/runtime/production/testdata/";

    /**
     * Test method for {@link HostingIGoogleUtil#uploadFile(
     * String, String, IgCredentials, String, String, String)} etc.
     *
     * @throws HostingException
     */
    @Test
    public void testAuthenticationAndUploadAndRetrieveFiles()
            throws HostingException {
        // Prepare Authentication.
        String emailUserName = "osde.test.001";
        String password = "osdetest888";
        String sid = IgCredentials.retrieveSid(emailUserName, password, null, null);
        logger.info("sid: " + sid);
        assertTrue(sid.length() > 50);
        String publicId = retrievePublicId(sid);
        logger.info("publicId: " + publicId);
        assertTrue(publicId.length() > 20);
        IgCredentials igCredentials = IgCredentials.retrieveIgCredentials(sid);
        assertTrue("Invalid igCredentials: " + igCredentials, igCredentials.validate());

        // Upload file.
        HostingIGoogleUtil.uploadFiles(
                sid, publicId, igCredentials, TEST_DATA_PATH, DUMMY_HOST_FOLDER);

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
        String hostedFileUrl = formHostedFileUrl(publicId, DUMMY_HOST_FOLDER, relativeFilePath);
        String fileContent = sendHttpRequestToIg(hostedFileUrl, sid);
        logger.info("fileContent:\n" + fileContent);
        assertTrue(fileContent.startsWith("<?xml version"));
        String previewUrl = formPreviewLegacyGadgetUrl(hostedFileUrl, false);
        logger.info("previewUrl: " + previewUrl);
        assertTrue(previewUrl.endsWith(relativeFilePath));

        // Delete the gadget file.
        String fileName = DUMMY_HOST_FOLDER + relativeFilePath;
        HostingIGoogleUtil.deleteFile(sid, publicId, fileName, igCredentials);
        // TODO: (p0) Assert something for delete.
    }

    /**
     * Test method for {@link HostingIGoogleUtil#findAllRelativeFilePaths(String)}.
     */
    @Test
    public void testFindAllRelativeFilePaths() {
        List<String> filePaths = findAllRelativeFilePaths(TEST_DATA_PATH);

        // Verify the 6 testing files are found.
        assertEquals(6, filePaths.size());
        for (String filePath : filePaths) {
            logger.info("filePath: " + filePath);
        }
    }
}
