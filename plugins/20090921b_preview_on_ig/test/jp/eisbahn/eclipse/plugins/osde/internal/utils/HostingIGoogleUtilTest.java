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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.IgPrefEditToken;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.*;

/**
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Annotate test as large.
public class HostingIGoogleUtilTest {

    private static Logger logger = Logger.getLogger(HostingIGoogleUtil.class.getName());

    /**
     * Test method for {@link HostingIGoogleUtil#uploadFile(
     * String, String, IgPrefEditToken, File, String)}.
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Test
    public void testAllMethods() throws ClientProtocolException, IOException {
        // TODO: Assert responses.

        // Prepare Authentication.
        String emailUserName = "osde.test.001";
        String password = "osdetest888";
        String sid = retrieveSid(emailUserName, password, null, null);
        logger.info("sid: " + sid);
        String publicId = retrievePublicId(sid);
        logger.info("publicId: " + publicId);
        IgPrefEditToken prefEditToken = retrieveIgPrefEditToken(sid);
        logger.info("pref: " + prefEditToken.getPref());
        logger.info("editToken: " + prefEditToken.getEditToken());

        // Upload file.
        File sourceFile =
            new File("test/jp.eisbahn.eclipse.plugins.osde.internal/utils/dummy_gadget.xml");
        String targetFilePath = "dummy_gadget.xml";
        String uploadFileResult =
            uploadFile(sid, publicId, prefEditToken, sourceFile, targetFilePath);
        logger.info("uploadFileResult: " + uploadFileResult);

        // Retrieve directory info.
        String quotaByte = retrieveQuotaByte(sid, publicId);
        logger.info("quotaByte: " + quotaByte);
        String quotaByteUsed = retrieveQuotaByteUsed(sid, publicId);
        logger.info("quotaByteUsed: " + quotaByteUsed);
        String fileList = retrieveFileList(sid, publicId);
        logger.info("fileList:\n" + fileList);
        String fileContent = retrieveFile(sid, publicId, targetFilePath);
        logger.info("fileContent:\n" + fileContent);
        String previewUrl = formPreviewGadgetUrl(publicId, targetFilePath);
        logger.info("previewUrl: " + previewUrl);
    }

}
