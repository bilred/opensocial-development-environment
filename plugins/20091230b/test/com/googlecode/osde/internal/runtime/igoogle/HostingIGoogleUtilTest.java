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
package com.googlecode.osde.internal.runtime.igoogle;

import java.util.List;

import com.googlecode.osde.internal.utils.Logger;

import org.junit.Test;

import static com.googlecode.osde.internal.runtime.igoogle.HostingIGoogleUtil.*;
import static org.junit.Assert.*;

/**
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Annotate test as large.
// TODO: Update corresponding wiki docs.
public class HostingIGoogleUtilTest {

    private static Logger logger = new Logger(HostingIGoogleUtil.class);

    private static final String TEST_USERNAME = "osde.test.001";
    private static final String TEST_PASSWORD = "osdetest888";
    private static final String TEST_TARGET_PATH =
            "test/com/googlecode/osde/internal/runtime/production/testdata/";
    private static final String GADGET_XML_FILE_RELATIVE_PATH = "gadget.xml";
    private static final String DUMMY_HOST_FOLDER = "/dummy_host_folder/";
    private static final String URL_SAMPLE_OPEN_SOCIAL_GADGET_FILE =
            "http://opensocial-resources.googlecode.com/"
            + "svn/samples/tutorial/tags/api-0.8/gifts_1_friends.xml";

    /**
     * Test method for {@link HostingIGoogleUtil#uploadFile(
     * String, String, IgCredentials, String, String, String)} etc.
     *
     * @throws HostingException
     */
    @Test
    public void testAuthenticationAndUploadAndRetrieveAndDeleteFiles()
            throws HostingException {
        // Prepare Authentication.
        String sid = IgCredentials.retrieveSid(TEST_USERNAME, TEST_PASSWORD, null, null);
        logger.info("sid: " + sid);
        assertTrue(sid.length() > 50);
        String publicId = retrievePublicId(sid);
        logger.info("publicId: " + publicId);
        assertTrue(publicId.length() > 20);
        IgCredentials igCredentials = IgCredentials.retrieveIgCredentials(sid);
        assertTrue("Invalid igCredentials: " + igCredentials, igCredentials.validate());

        // Upload file.
        HostingIGoogleUtil.uploadFiles(
                sid, publicId, igCredentials, TEST_TARGET_PATH, DUMMY_HOST_FOLDER);

        // Retrieve directory/file info.
        String quotaByte = retrieveQuotaByte(sid, publicId);
        logger.info("quotaByte: " + quotaByte);
        String quotaByteUsed = retrieveQuotaByteUsed(sid, publicId);
        logger.info("quotaByteUsed: " + quotaByteUsed);
        assertTrue(Integer.valueOf(quotaByteUsed) > 100); // dummy_gadget.xml size > 100
        String fileList = retrieveFileList(sid, publicId);
        logger.info("fileList:\n" + fileList);
        assertTrue(fileList.length() > 50);
        String hostedFileUrl =
                formHostedFileUrl(publicId, DUMMY_HOST_FOLDER, GADGET_XML_FILE_RELATIVE_PATH);
        String fileContent = sendHttpRequestToIg(hostedFileUrl, sid);
        logger.info("fileContent:\n" + fileContent);
        assertTrue(fileContent.startsWith("<?xml version"));

        // Delete the uploaded gadget file.
        String fileName = DUMMY_HOST_FOLDER + GADGET_XML_FILE_RELATIVE_PATH;
        HostingIGoogleUtil.deleteFile(sid, publicId, fileName, igCredentials);
        // TODO: (p0) Assert something for delete.
    }

    /**
     * Test method for {@link HostingIGoogleUtil#formPreviewLegacyGadgetUrl(String, boolean)}
     * and {@link HostingIGoogleUtil#formPreviewOpenSocialGadgetUrl(String, boolean, String)}.
     *
     * @throws HostingException
     */
    @Test
    public void testFormPreviewUrl()
            throws HostingException {
        // Prepare Authentication.
        // TODO: Share this block of code with other test methods.
        String sid = IgCredentials.retrieveSid(TEST_USERNAME, TEST_PASSWORD, null, null);
        logger.info("sid: " + sid);
        assertTrue(sid.length() > 50);
        String publicId = retrievePublicId(sid);
        logger.info("publicId: " + publicId);
        assertTrue(publicId.length() > 20);
        IgCredentials igCredentials = IgCredentials.retrieveIgCredentials(sid);
        assertTrue("Invalid igCredentials: " + igCredentials, igCredentials.validate());

        // Verify preview legacy gadget.
        String previewUrlForLegacyGadget =
                formPreviewLegacyGadgetUrl(URL_SAMPLE_OPEN_SOCIAL_GADGET_FILE, false);
        logger.info("previewUrlForLegacyGadget: " + previewUrlForLegacyGadget);
        assertTrue(previewUrlForLegacyGadget.endsWith(URL_SAMPLE_OPEN_SOCIAL_GADGET_FILE));

        // Verify preview OpenSocial gadget.
        boolean useCanvasView = false;
        String previewUrlForOpenSocialGadget = formPreviewOpenSocialGadgetUrl(
                URL_SAMPLE_OPEN_SOCIAL_GADGET_FILE, useCanvasView, sid);
        logger.info("previewUrlForOpenSocialGadget:" + previewUrlForOpenSocialGadget);
        // TODO: assert sth when server is ready for formPreviewOpenSocialGadgetUrl().
    }

    /**
     * Test method for {@link HostingIGoogleUtil#findAllRelativeFilePaths(String)}.
     */
    @Test
    public void testFindAllRelativeFilePaths() {
        List<String> filePaths = findAllRelativeFilePaths(TEST_TARGET_PATH);

        // Verify the 6 testing files are found.
        assertEquals(6, filePaths.size());
        for (String filePath : filePaths) {
            logger.info("filePath: " + filePath);
        }
    }
}