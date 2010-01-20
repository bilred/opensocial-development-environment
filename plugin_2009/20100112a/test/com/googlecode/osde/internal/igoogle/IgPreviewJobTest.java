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

import com.googlecode.osde.internal.igoogle.IgBaseJob;
import com.googlecode.osde.internal.igoogle.HostingException;
import com.googlecode.osde.internal.igoogle.IgPreviewJob;
import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

/**
 * @author albert.cheng.ig@gmail.com
 */
public class IgPreviewJobTest {

    private static Logger logger = new Logger(IgPreviewJobTest.class);
    private static final String TEST_USERNAME = "osde.test.001";
    private static final String TEST_PASSWORD = "osdetest888";
    private static final String TEST_TARGET_PATH =
            "test/com/googlecode/osde/internal/igoogle/testdata/";
    private static final String TEST_HOSTING_FOLDER = "/osde/test/";
    private static final String GADGET_XML_FILE_RELATIVE_PATH = "gadget.xml";

    /**
     * Test method for {@link IgBaseJob#uploadFilesToIg(String, boolean)}.
     *
     * @throws HostingException
     */
    @Test
    public void testUploadFilesToIg()
            throws HostingException {
        // Prepare mocks.
        IFile gadgetXmlIFile = createMock(IFile.class);
        IProject project = createMock(IProject.class);
        IFolder targetFolder = createMock(IFolder.class);
        IPath targetFolderLocation = createMock(IPath.class);
        expect(gadgetXmlIFile.getName()).andReturn(GADGET_XML_FILE_RELATIVE_PATH).anyTimes();
        expect(gadgetXmlIFile.getProject()).andReturn(project).anyTimes();
        expect(project.getFolder(eq(IgBaseJob.TARGET_FOLDER_NAME)))
                .andReturn(targetFolder).anyTimes();
        expect(targetFolder.getLocation()).andReturn(targetFolderLocation).anyTimes();
        expect(targetFolderLocation.toOSString()).andReturn(TEST_TARGET_PATH);
        replay(gadgetXmlIFile, project, targetFolder, targetFolderLocation);

        // Prepare job.
        Shell shell = new Shell();
        IgPreviewJob job = new IgPreviewJob(
                TEST_USERNAME, TEST_PASSWORD, gadgetXmlIFile, shell, false, false);

        // Test and verify the method call.
        String urlOfHostedGadgetFile = job.uploadFilesToIg(TEST_HOSTING_FOLDER, false);
        logger.info("urlOfHostedGadgetFile: " + urlOfHostedGadgetFile);
        assertTrue(urlOfHostedGadgetFile.endsWith("/" + GADGET_XML_FILE_RELATIVE_PATH));
        verify(gadgetXmlIFile, project, targetFolder, targetFolderLocation);
    }
}
