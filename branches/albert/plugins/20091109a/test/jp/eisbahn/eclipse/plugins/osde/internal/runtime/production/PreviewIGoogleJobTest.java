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

import static jp.eisbahn.eclipse.plugins.osde.internal.runtime.production.PreviewIGoogleJob.OSDE_PREVIEW_DIRECTORY;
import static jp.eisbahn.eclipse.plugins.osde.internal.runtime.production.PreviewIGoogleJob.TARGET_FOLDER_NAME;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * @author albert.cheng.ig@gmail.com
 */
public class PreviewIGoogleJobTest {

    private static Logger logger = Logger.getLogger(PreviewIGoogleJobTest.class.getName());

    /**
     * Test method for {@link PreviewIGoogleJob#run(org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
    public void testRunIProgressMonitor() {
        // TODO: Implement testRunIProgressMonitor()
    }

    /**
     * Test method for {@link PreviewIGoogleJob#uploadFilesToIg(String, boolean)}.
     * @throws HostingException
     */
    @Test
    public void testUploadFilesToIg()
            throws HostingException {
        String targetPath = "test/jp/eisbahn/eclipse/plugins/osde/internal/runtime/"
            + "production/testdata/";
        String gadgetXmlFileRelativePath = "gadget.xml";

        // Prepare mocks.
        IFile gadgetXmlIFile = createMock(IFile.class);
        IProject project = createMock(IProject.class);
        IFolder targetFolder = createMock(IFolder.class);
        IPath targetFolderLocation = createMock(IPath.class);
        expect(gadgetXmlIFile.getName()).andReturn(gadgetXmlFileRelativePath).anyTimes();
        expect(gadgetXmlIFile.getProject()).andReturn(project).anyTimes();
        expect(project.getFolder(eq(TARGET_FOLDER_NAME))).andReturn(targetFolder).anyTimes();
        expect(targetFolder.getLocation()).andReturn(targetFolderLocation).anyTimes();
        expect(targetFolderLocation.toOSString()).andReturn(targetPath);
        replay(gadgetXmlIFile, project, targetFolder, targetFolderLocation);

        // Prepare job.
        Shell shell = new Shell();
        String username = "osde.test.001";
        String password = "osdetest888";
        PreviewIGoogleJob job =
                new PreviewIGoogleJob(username, password, gadgetXmlIFile, shell, false, false);

        // Test and verify the method call.
        String gadgetPreviewUrl = job.uploadFilesToIg(OSDE_PREVIEW_DIRECTORY, false);
        logger.info("gadgetPreviewUrl: " + gadgetPreviewUrl);
        assertTrue(gadgetPreviewUrl.endsWith(gadgetXmlFileRelativePath));
        verify(gadgetXmlIFile, project, targetFolder, targetFolderLocation);
    }
}
