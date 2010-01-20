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

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.formHostedFileUrl;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrieveIgPrefEditToken;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrievePublicId;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.retrieveSid;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil.uploadFiles;

import java.util.logging.Logger;

import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingException;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.HostingIGoogleUtil;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.IgCredentials;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;

/**
 * The base job for iGoogle usage.
 *
 * @author albert.cheng.ig@gmail.com
 */
// TODO: The methods in this class might better be static,
// and thus some variable(s) and constructor(s) might be unnecessary.
// In other words, we might think about making this class a utility
// class instead of a parent class.
public abstract class BaseIGoogleJob extends Job {
    private static Logger logger = Logger.getLogger(BaseIGoogleJob.class.getName());

    static final String OSDE_PREVIEW_DIRECTORY = "/osde/preview/";
    static final String OSDE_PUBLISH_DIRECTORY = "/osde/publish/";

    /**
     * This constant stands for the folder name of "target".
     * This folder stores all the gadget-related files.
     * This folder is created in
     * {@link jp.eisbahn.eclipse.plugins.osde.internal.builders.GadgetBuilder GadgetBuilder}
     * which determines what files are for deployment purpose.
     */
    // TODO: Reduce the impact caused by changes in GadgetBuilder:
    // The first approach is to make this a global constant and make
    // sure every corresponding code calls this constant instead of
    // using a string literal "target".
    // By doing this, any change in GadgetBuilder can be reflected to
    // all the places immediately, and thus reduce the impact from
    // changes in one place.
    // Also, we need to make sure this folder exists prior to using
    // it here.
    static final String TARGET_FOLDER_NAME = "target";

    private String username;
    private String password;
    private IFile gadgetXmlIFile;

    public BaseIGoogleJob(String jobName, String username, String password, IFile gadgetXmlIFile) {
        super(jobName);
        this.username = username;
        this.password = password;
        this.gadgetXmlIFile = gadgetXmlIFile;
    }

    public BaseIGoogleJob(String jobName, String username, String password) {
        this(jobName, username, password, null);
    }

    /**
     * Uploads files to iGoogle and returns the url for the hosted gadget file.
     *
     * @return the url for the hosted gadget file
     * @throws HostingException
     */
    protected String uploadFilesToIg(String hostingFolder, boolean useCanvasView)
            throws HostingException {
        // TODO: Support save SID etc in session.
        // TODO: Support captcha.
        logger.fine("in PreviewIGoogleJob.uploadFilesToIg");

        // Prepare params.
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgCredentials igCredentials = retrieveIgPrefEditToken(sid);
        IProject project = gadgetXmlIFile.getProject();
        String uploadFromPath = project.getFolder(TARGET_FOLDER_NAME).getLocation().toOSString();

        // Upload files.
        uploadFiles(sid, publicId, igCredentials, uploadFromPath, hostingFolder);

        // Form hosted gadget file url.
        String urlOfHostedGadgetFile =
                formHostedFileUrl(publicId, hostingFolder, gadgetXmlIFile.getName());
        return urlOfHostedGadgetFile;
    }

    protected void cleanFiles(String hostingFolder) throws HostingException {
        // TODO: Make the following "prepare params" function common
        // to this method and uploadFilesToIg().

        // Prepare params.
        String sid = retrieveSid(username, password, null, null);
        String publicId = retrievePublicId(sid);
        IgCredentials igCredentials = retrieveIgPrefEditToken(sid);

        // Clean files.
        HostingIGoogleUtil.cleanFiles(sid, publicId, igCredentials, hostingFolder);
    }
}