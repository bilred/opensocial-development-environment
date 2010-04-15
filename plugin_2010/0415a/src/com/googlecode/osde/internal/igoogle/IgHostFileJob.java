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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The job to host gadget files to iGoogle.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgHostFileJob extends Job {
    private static Logger logger = new Logger(IgHostFileJob.class);
    static final String OSDE_HOST_BASE_FOLDER = "/osde/";
    static final String LOCAL_HOST_URL = "http://localhost:8080/";
    static final String GADGET_FILE_WITH_MODIFIED_URL = "modified_gadget.xml";

    private String username;
    private String password;
	private String hostProjectName;
    private IFile gadgetXmlIFile;
    private Shell shell;

    public IgHostFileJob(String username, String password, String hostProjectName,
    		IFile gadgetXmlIFile, Shell shell) {
        super("iGoogle - Host Gadget Files");
        this.username = username;
        this.password = password;
        this.hostProjectName = hostProjectName;
        this.gadgetXmlIFile = gadgetXmlIFile;
        this.shell = shell;
    }

    protected IStatus run(final IProgressMonitor monitor) {
        logger.fine("in run");
        monitor.beginTask("Running IgHostFileJob", 3);

        List<String> relativeFilePathsOfHostedFiles = null;
        String hostingUrl = null;
        try {
            IgCredentials igCredentials = new IgCredentials(username, password);

            // Get hosting URL.
            String hostingFolder = OSDE_HOST_BASE_FOLDER + hostProjectName + "/";
            hostingUrl = IgHostingUtil.formHostingUrl(igCredentials.getPublicId(), hostingFolder);

            // Modify gadget file with new hosting url, and upload it.
            String eclipseProjectName = gadgetXmlIFile.getProject().getName();
            String oldHostingUrl = LOCAL_HOST_URL + eclipseProjectName + "/";
            modifyHostingUrlForGadgetFileAndUploadIt(oldHostingUrl, hostingUrl, igCredentials,
            		hostingFolder);

            // Upload files.
            relativeFilePathsOfHostedFiles = IgHostingUtil.uploadFiles(
            		igCredentials, gadgetXmlIFile.getProject(), hostingFolder);
        } catch (IgException e) {
            logger.warn(e.getMessage());
            monitor.setCanceled(true);
            return Status.CANCEL_STATUS;
        }
        monitor.worked(1);

        Display display = shell.getDisplay();
        monitor.worked(1);

        display.syncExec(new HostingFileRunnable(hostingUrl, relativeFilePathsOfHostedFiles));
        monitor.worked(1);

        monitor.done();
        return Status.OK_STATUS;
    }

    /**
     * Replaces oldHostingUrl with newHostingUrl in the gadgetXmlIFile,
     * which is stored in a temporary file.
     * Then upload the temporary file to iGoogle.
     *
     * @throws IgException
     */
    void modifyHostingUrlForGadgetFileAndUploadIt(String oldHostingUrl, String newHostingUrl,
            IgCredentials igCredentials, String hostingFolder)
            throws IgException {
        // Get gadget file's full path.
        IProject project = gadgetXmlIFile.getProject();
        String targetFolder =
            project.getFolder(GadgetBuilder.TARGET_FOLDER_NAME).getLocation().toOSString();
        String gadgetFileFullPath = targetFolder + "/" + gadgetXmlIFile.getName();
        logger.fine("gadgetFileFullPath: " + gadgetFileFullPath);

        // Read content from gadget file, and then modify them and save as a new gadget file.
        // TODO: Do we need to specify the encoding for the pair of reader and writer?
        FileReader originalGadgetXmlFileReader = null;
        FileWriter modifiedGadgetXmlFileWriter = null;
        try {
            originalGadgetXmlFileReader = new FileReader(gadgetFileFullPath);
            String fileContentAsString = IOUtils.toString(originalGadgetXmlFileReader);
            logger.fine("fileContentAsString:\n" + fileContentAsString);
            String modifiedFileContent =
                    fileContentAsString.replaceAll(oldHostingUrl, newHostingUrl);

            // Prepare the modified gadget file.
            File osdeWorkFolder = getOsdeWorkFolder();
            File modifiedFile = new File(osdeWorkFolder, GADGET_FILE_WITH_MODIFIED_URL);
            if (modifiedFile.exists()) {
                modifiedFile.delete();
            }
            boolean isCreated = modifiedFile.createNewFile();
            logger.fine("isCreated: " + isCreated);

            // Write modified content to the new gadget file.
            modifiedGadgetXmlFileWriter = new FileWriter(modifiedFile);
            modifiedGadgetXmlFileWriter.write(modifiedFileContent);
            modifiedGadgetXmlFileWriter.flush();

            // Upload the modified gadget file to iGoogle.
            IgHostingUtil.uploadFile(igCredentials, osdeWorkFolder.getAbsolutePath(),
                    GADGET_FILE_WITH_MODIFIED_URL, hostingFolder);
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new IgException(e);
        } finally {
            IOUtils.closeQuietly(originalGadgetXmlFileReader);
            IOUtils.closeQuietly(modifiedGadgetXmlFileWriter);
        }
    }

    // TODO: Move this to Activator.
    static File getOsdeWorkFolder() {
        String userHome = System.getProperty("user.home");
        File osdeWorkFolder = new File(userHome, Activator.WORK_DIR_NAME);
        if (!osdeWorkFolder.exists()) {
            osdeWorkFolder.mkdir();
        }
        return osdeWorkFolder;
    }

    private class HostingFileRunnable implements Runnable {
    	String hostingUrl;
    	List<String> relativeFilePathsOfHostedFiles;

        private HostingFileRunnable(String hostingUrl,
        		List<String> relativeFilePathsOfHostedFiles) {
        	this.hostingUrl = hostingUrl;
        	this.relativeFilePathsOfHostedFiles = relativeFilePathsOfHostedFiles;
        }

        public void run() {
            logger.fine("in Runnable's run");
            String dialogTitle = "Your Files Are Hosted.";
            
            // For dialog message
            StringBuilder dialogMessage = new StringBuilder();
            dialogMessage.append("All your following gadget files:\n");
            for (String urlOfHostedFile : relativeFilePathsOfHostedFiles) {
            	dialogMessage.append(urlOfHostedFile).append("\n");
            }
            dialogMessage.append("\nand the gadget file for preview: ");
            dialogMessage.append(GADGET_FILE_WITH_MODIFIED_URL);
            dialogMessage.append("\nare hosted at this URL:\n");
            dialogMessage.append(hostingUrl);
            
            int dialogImageType = MessageDialog.INFORMATION;
            Image dialogTitleImage = null;
            String[] dialogButtonLabels = {"OK"};
            int defaultIndex = 0;
            MessageDialog dialog = new MessageDialog(shell, dialogTitle, dialogTitleImage,
                    dialogMessage.toString(), dialogImageType, dialogButtonLabels, defaultIndex);
            int openResult = dialog.open();
            logger.fine("openResult: " + openResult);
        }
    }
}