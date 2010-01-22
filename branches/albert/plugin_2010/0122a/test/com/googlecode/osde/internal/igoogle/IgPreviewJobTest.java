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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.utils.Logger;

/**
 * @author albert.cheng.ig@gmail.com
 */
public class IgPreviewJobTest {

    private static Logger logger = new Logger(IgPreviewJobTest.class);

    private static final String TEST_TARGET_PATH =
            "test/com/googlecode/osde/internal/igoogle/testdata/";
    private static final String GADGET_XML_FILE_RELATIVE_PATH = "gadget.xml";

    @Test
    public final void testModifyHostingUrlForGadgetFileInTargetFolder() {
        // Prepare mocks.
        IFile gadgetXmlIFile = createMock(IFile.class);
        IProject project = createMock(IProject.class);
        IFolder targetFolder = createMock(IFolder.class);
        IPath targetFolderLocation = createMock(IPath.class);
        expect(gadgetXmlIFile.getProject()).andReturn(project).anyTimes();
        expect(project.getFolder(eq(GadgetBuilder.TARGET_FOLDER_NAME)))
                .andReturn(targetFolder).anyTimes();
        expect(targetFolder.getLocation()).andReturn(targetFolderLocation).anyTimes();
        expect(targetFolderLocation.toOSString()).andReturn(TEST_TARGET_PATH);
        expect(gadgetXmlIFile.getName()).andReturn(GADGET_XML_FILE_RELATIVE_PATH).anyTimes();
        replay(gadgetXmlIFile, project, targetFolder, targetFolderLocation);

        IgPreviewJob job = new IgPreviewJob(null, null, gadgetXmlIFile, null, false, false);
        String newHostingUrl = "http://some.new.host/blah_blah/";
        job.modifyHostingUrlForGadgetFileInTargetFolder(newHostingUrl);

        verify(gadgetXmlIFile, project, targetFolder, targetFolderLocation);
    }

}
