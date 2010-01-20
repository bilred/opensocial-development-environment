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

import java.util.List;

import com.googlecode.osde.internal.igoogle.HostingException;
import com.googlecode.osde.internal.igoogle.IgHostingUtil;
import com.googlecode.osde.internal.igoogle.IgCredentials;
import com.googlecode.osde.internal.utils.Logger;

import org.junit.Test;

import static com.googlecode.osde.internal.igoogle.IgHostingUtil.findAllRelativeFilePaths;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formHostedFileUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formPreviewLegacyGadgetUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formPreviewOpenSocialGadgetUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveFileNameList;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrievePublicId;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveQuotaByte;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveQuotaByteUsed;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.sendHttpRequestToIg;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Annotate test as large.
// TODO: Update corresponding wiki docs.
public class IgHostingUtilTest {

    private static Logger logger = new Logger(IgHostingUtil.class);

    private static final String TEST_USERNAME = "osde.test.001";
    private static final String TEST_PASSWORD = "osdetest888";
    private static final String TEST_TARGET_PATH =
            "test/com/googlecode/osde/internal/igoogle/testdata/";
    private static final String TEST_HOSTING_FOLDER = "/osde/test/";
    private static final String GADGET_XML_FILE_RELATIVE_PATH = "gadget.xml";
    private static final String TEST_GADGET_FILE_START_CONTENT = "<?xml version";
    private static final int TEST_HOSTED_FILES_COUNT = 6;
    private static final int MIN_QUOTA_BYTE = 1000000;
    private static final int TEST_MIN_QUOTA_BYTE_USED = 1000;
    private static final String URL_SAMPLE_OPEN_SOCIAL_GADGET_FILE =
            "http://opensocial-resources.googlecode.com/"
                    + "svn/samples/tutorial/tags/api-0.8/gifts_1_friends.xml";

    /**
     * Test method for {@link IgHostingUtil#uploadFile(
     *String, String, IgCredentials, String, String, String)} etc.
     *
     * @throws HostingException
     */
    @Test
    public void testLifeCycleForHostedFile()
            throws HostingException {
        // Prepare Authentication.
        String sid = IgCredentials.retrieveSid(TEST_USERNAME, TEST_PASSWORD, null, null);
        assertEquals(IgCredentials.SID_LENGTH, sid.length());
        String publicId = retrievePublicId(sid);
        assertEquals(IgCredentials.PUBLIC_ID_LENGTH, publicId.length());
        IgCredentials igCredentials = IgCredentials.retrieveIgCredentials(sid);
        assertTrue("Invalid igCredentials: " + igCredentials, igCredentials.validate());

        // Clean all files under TEST_HOSTING_FOLDER.
        IgHostingUtil.cleanFiles(sid, publicId, igCredentials, TEST_HOSTING_FOLDER);
        List<String> fileNameList = retrieveFileNameList(sid, publicId, TEST_HOSTING_FOLDER);
        assertEquals(0, fileNameList.size());

        // Upload file.
        IgHostingUtil.uploadFiles(
                sid, publicId, igCredentials, TEST_TARGET_PATH, TEST_HOSTING_FOLDER);

        // Retrieve quota info.
        String quotaByte = retrieveQuotaByte(sid, publicId);
        logger.fine("quotaByte: " + quotaByte);
        assertTrue(Integer.valueOf(quotaByte) > MIN_QUOTA_BYTE);
        String quotaByteUsed = retrieveQuotaByteUsed(sid, publicId);
        logger.fine("quotaByteUsed: " + quotaByteUsed);
        assertTrue(Integer.valueOf(quotaByteUsed) > TEST_MIN_QUOTA_BYTE_USED);

        // Retrieve all files list.
        fileNameList = retrieveFileNameList(sid, publicId, TEST_HOSTING_FOLDER);
        assertEquals(TEST_HOSTED_FILES_COUNT, fileNameList.size());

        // Retrieve file content.
        String hostedFileUrl =
                formHostedFileUrl(publicId, TEST_HOSTING_FOLDER, GADGET_XML_FILE_RELATIVE_PATH);
        String fileContent = sendHttpRequestToIg(hostedFileUrl, sid);
        logger.fine("fileContent:\n" + fileContent);
        assertTrue(fileContent.startsWith(TEST_GADGET_FILE_START_CONTENT));
    }

    /**
     * Test method for {@link IgHostingUtil#formPreviewLegacyGadgetUrl(String, boolean)}
     * and {@link IgHostingUtil#formPreviewOpenSocialGadgetUrl(String, boolean, String)}.
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
     * Test method for {@link IgHostingUtil#findAllRelativeFilePaths(String)}.
     */
    @Test
    public void testFindAllRelativeFilePaths() {
        List<String> filePaths = findAllRelativeFilePaths(TEST_TARGET_PATH);

        // Verify the testing files are found.
        assertEquals(TEST_HOSTED_FILES_COUNT, filePaths.size());
        for (String filePath : filePaths) {
            logger.info("filePath: " + filePath);
        }
    }
}
