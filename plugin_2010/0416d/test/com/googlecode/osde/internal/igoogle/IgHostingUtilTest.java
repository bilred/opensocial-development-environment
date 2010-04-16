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

import static com.googlecode.osde.internal.igoogle.IgHostingUtil.cleanFiles;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.findAllRelativeFilePaths;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formHostingUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formPreviewLegacyGadgetUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.formPreviewOpenSocialGadgetUrl;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveFileNameList;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveHttpResponseAsString;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveQuotaByte;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.retrieveQuotaByteUsed;
import static com.googlecode.osde.internal.igoogle.IgHostingUtil.uploadFiles;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.utils.Logger;

/**
 * @author albert.cheng.ig@gmail.com
 */
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

    // This sample gadget file demos OpenSocial feature of showing friends list.
    // But it does not specify any view. So only "home" view is supported.
    private static final String URL_SAMPLE_OS_GADGET_FILE_WITH_HOME_VIEW =
            "http://opensocial-resources.googlecode.com/"
                    + "svn/samples/tutorial/tags/api-0.8/gifts_1_friends.xml";

    /**
     * Test method for
     * {@link IgHostingUtil#cleanFiles(IgCredentials, String)},
     * {@link IgHostingUtil#formHostingUrl(String, String)},
     * {@link IgHostingUtil#uploadFiles(IgCredentials, String, String)},
     * {@link IgHostingUtil#retrieveQuotaByte(String, String)},
     * {@link IgHostingUtil#retrieveQuotaByteUsed(String, String)},
     * {@link IgHostingUtil#retrieveFileNameList(String, String, String)},
     * {@link IgHostingUtil#retrieveHttpResponseAsString(String, String)},
     * etc.
     *
     * @throws IgException
     */
    @Test
    public void testLifeCycleForHostedFile()
            throws IgException {
        // Prepare Authentication.
        IgCredentials igCredentials =
    	        IgCredentials.createCurrentInstance(TEST_USERNAME, TEST_PASSWORD);
        String sid = igCredentials.getSid();
        logger.info("sid: " + sid);
        String publicId = igCredentials.getPublicId();
        logger.info("publicId: " + publicId);

        // Clean all files under TEST_HOSTING_FOLDER.
        cleanFiles(igCredentials, TEST_HOSTING_FOLDER);
        List<String> fileNameList = retrieveFileNameList(sid, publicId, TEST_HOSTING_FOLDER);
        assertEquals(0, fileNameList.size());

        // Retrieve quota info.
        String quotaByte = retrieveQuotaByte(sid, publicId);
        logger.info("quotaByte: " + quotaByte);
        assertTrue(Integer.valueOf(quotaByte) > MIN_QUOTA_BYTE);
        String quotaByteUsed = retrieveQuotaByteUsed(sid, publicId);
        logger.info("quotaByteUsed: " + quotaByteUsed);
        assertTrue(Integer.valueOf(quotaByteUsed) > TEST_MIN_QUOTA_BYTE_USED);

        // Upload file.
        uploadFiles(igCredentials, TEST_TARGET_PATH, TEST_HOSTING_FOLDER);

        // Retrieve all files list.
        fileNameList = retrieveFileNameList(sid, publicId, TEST_HOSTING_FOLDER);
        assertEquals(TEST_HOSTED_FILES_COUNT, fileNameList.size());

        // Retrieve file content.
        String hostedFileUrl = formHostingUrl(publicId, TEST_HOSTING_FOLDER)
                + GADGET_XML_FILE_RELATIVE_PATH;
        String fileContent = retrieveHttpResponseAsString(hostedFileUrl, sid);
        logger.fine("fileContent:\n" + fileContent);
        assertTrue(fileContent.startsWith(TEST_GADGET_FILE_START_CONTENT));
    }

    /**
     * Test method for
     * {@link IgHostingUtil#uploadFiles(IgCredentials, IProject, String, boolean)}.
     *
     * @throws IgException
     */
    @Test
    public void testUploadFilesToIg()
            throws IgException {
        // Prepare mocks.
        IProject project = createMock(IProject.class);
        IFolder targetFolder = createMock(IFolder.class);
        IPath targetFolderLocation = createMock(IPath.class);
        expect(project.getFolder(eq(GadgetBuilder.TARGET_FOLDER_NAME)))
                .andReturn(targetFolder).anyTimes();
        expect(targetFolder.getLocation()).andReturn(targetFolderLocation).anyTimes();
        expect(targetFolderLocation.toOSString()).andReturn(TEST_TARGET_PATH).anyTimes();
        replay(project, targetFolder, targetFolderLocation);

        // Test and verify the method call.
        IgCredentials igCredentials =
    	        IgCredentials.createCurrentInstance(TEST_USERNAME, TEST_PASSWORD);
        logger.info("igCredentials:\n" + igCredentials);
        uploadFiles(igCredentials, project, TEST_HOSTING_FOLDER);
        verify(project, targetFolder, targetFolderLocation);
    }

    /**
     * Test method for {@link IgHostingUtil#formPreviewLegacyGadgetUrl(String, boolean)}
     * and {@link IgHostingUtil#formPreviewOpenSocialGadgetUrl(String, boolean, String)}.
     *
     * @throws IgException
     */
    @Test
    public void testFormPreviewUrl()
            throws IgException {
        // Prepare Authentication.
        IgCredentials igCredentials =
    	        IgCredentials.createCurrentInstance(TEST_USERNAME, TEST_PASSWORD);

        // Verify preview legacy gadget.
        String previewUrlForLegacyGadget =
                formPreviewLegacyGadgetUrl(URL_SAMPLE_OS_GADGET_FILE_WITH_HOME_VIEW, false);
        logger.info("previewUrlForLegacyGadget: " + previewUrlForLegacyGadget);
        assertTrue(previewUrlForLegacyGadget.endsWith(URL_SAMPLE_OS_GADGET_FILE_WITH_HOME_VIEW));

        // Verify preview OpenSocial gadget.
        boolean useCanvasView = false;
        String previewUrlForOpenSocialGadget = formPreviewOpenSocialGadgetUrl(
                URL_SAMPLE_OS_GADGET_FILE_WITH_HOME_VIEW, useCanvasView, igCredentials.getSid());
        logger.info("previewUrlForOpenSocialGadget:\n" + previewUrlForOpenSocialGadget);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.startsWith("http://"));
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf(URL_SAMPLE_OS_GADGET_FILE_WITH_HOME_VIEW)
                != -1);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf("view=home") != -1);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf("nocache=1") != -1);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf("gadgetId=") != -1);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf("gadgetOwner=") != -1);
        assertTrue(previewUrlForOpenSocialGadget,
                previewUrlForOpenSocialGadget.indexOf("gadgetViewer=") != -1);
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
