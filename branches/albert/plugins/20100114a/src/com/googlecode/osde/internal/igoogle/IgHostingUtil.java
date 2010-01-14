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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.internal.igoogle.IgHostingUtilTest;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * This iGoogle utility class provides hosting-related methods to
 * interact with iGoogle (http://www.google.com/ig) gadget container.
 * <p>
 * Samples of usages could be found at
 * {@link IgHostingUtilTest#testLifeCycleForHostedFile()} etc.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgHostingUtil {

    private static Logger logger = new Logger(IgHostingUtil.class);

    private static final String HTTP_PLAIN_TEXT_TYPE =
            HTTP.PLAIN_TEXT_TYPE + HTTP.CHARSET_PARAM + IgHttpUtil.ENCODING;

    private static final String URL_IG_GADGETS_BYTE_QUOTA =
            IgHttpUtil.URL_IG_GADGETS + "/bytequota/";
    private static final String URL_IG_GADGETS_BYTES_USED =
            IgHttpUtil.URL_IG_GADGETS + "/bytesused/";
    private static final String URL_IG_GADGETS_DIRECTORY =
            IgHttpUtil.URL_IG_GADGETS + "/directory/";
    private static final String URL_IG_GADGETS_FILE = IgHttpUtil.URL_IG_GADGETS + "/file/";
    private static final String URL_IG_PREVIEW_OPEN_SOCIAL_GADGET =
            IgHttpUtil.URL_HTTP_IG + "/iframeurl?moduleurl=";
    private static final String URL_IG_SUBMIT_GADGET =
            IgHttpUtil.URL_HTTP_IG + "/submit?prefill_url=";

    private static final String URL_GMODULE_FILE = "http://hosting.gmodules.com/ig/gadgets/file/";
    private static final String URL_PREVIEW_LEGACY_GADGET =
            "http://www.gmodules.com/gadgets/ifr?nocache=1";

    private static final String PREVIEW_URL_IDENTIFIER_FOR_HOME = "hifr:\"";
    private static final String PREVIEW_URL_IDENTIFIER_FOR_CANVAS = "cifr:\"";
    private static final String PREVIEW_URL_END_IDENTIFIER = "&is_social=1";
    private static final int PREVIEW_URL_END_IDENTIFIER_LENGTH =
            PREVIEW_URL_END_IDENTIFIER.length();

    private IgHostingUtil() {
        // Disable instantiation of this utility class.
    }

    /**
     * Uploads files to iGoogle and returns the url for the hosted gadget file.
     *
     * @return the url for the hosted gadget file
     * @throws IgException
     */
    static String uploadFiles(IgCredentials igCredentials, IFile gadgetXmlIFile,
            String hostingFolder, boolean useCanvasView)
            throws IgException {
        logger.fine("in PreviewIGoogleJob.uploadFilesToIg");

        IProject project = gadgetXmlIFile.getProject();
        String uploadFromPath =
                project.getFolder(GadgetBuilder.TARGET_FOLDER_NAME).getLocation().toOSString();

        // Upload files.
        IgHostingUtil.uploadFiles(igCredentials, uploadFromPath, hostingFolder);

        // Form hosted gadget file url.
        String urlOfHostedGadgetFile = IgHostingUtil.formHostedFileUrl(igCredentials.getPublicId(),
                hostingFolder, gadgetXmlIFile.getName());
        return urlOfHostedGadgetFile;
    }

    /**
     * Uploads a file to iGoogle.
     *
     * @throws IgException
     */
    public static void uploadFile(IgCredentials igCredentials,
            String sourceFileRootPath, String sourceFileRelativePath, String hostingFolder)
            throws IgException {
        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + igCredentials.getPublicId() + hostingFolder
                + sourceFileRelativePath + "?et=" + igCredentials.getEditToken();
        logger.fine("url: " + url);
        HttpPost httpPost = new HttpPost(url);
        File sourceFile = new File(sourceFileRootPath, sourceFileRelativePath);
        String httpContentType = isTextExtensionForGadgetFile(sourceFileRelativePath)
                ? HTTP_PLAIN_TEXT_TYPE
                : HTTP.DEFAULT_CONTENT_TYPE;
        httpPost.setHeader(HTTP.CONTENT_TYPE, httpContentType);
        FileEntity fileEntity = new FileEntity(sourceFile, httpContentType);
        logger.fine("fileEntity length: " + fileEntity.getContentLength());
        httpPost.setEntity(fileEntity);

        // Add cookie headers. Cookie PREF must be placed before SID.
        httpPost.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "PREF=" + igCredentials.getPref());
        httpPost.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "SID=" + igCredentials.getSid());

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            throw new IgException(e);
        } catch (IOException e) {
            throw new IgException(e);
        }
        StatusLine statusLine = httpResponse.getStatusLine();
        logger.fine("statusLine: " + statusLine);
        if (HttpStatus.SC_CREATED != statusLine.getStatusCode()) {
            String response =
                    IgHttpUtil.retrieveHttpResponseAsString(httpClient, httpPost, httpResponse);
            throw new IgException("Failed file-upload with status line: "
                    + statusLine.toString() + "\nand response:\n" + response);
        }
    }

    private static boolean isTextExtensionForGadgetFile(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");

        // Return false if no dot found or the last char is dot.
        if ((lastIndexOfDot == -1)
                || (lastIndexOfDot == (fileName.length() - 1))) {
            return false;
        }

        // The following extensions in textExtensions are treated as text types.
        // It is ok to treat all the other extensions as default types.
        String[] textExtensions = new String[] {
                "xml", "js", "css", "html", "htm"
        };
        String extension = fileName.substring(lastIndexOfDot + 1).toLowerCase();

        for (String textExtension : textExtensions) {
            if (textExtension.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uploads a list of files to iGoogle.
     *
     * @throws IgException
     */
    public static void uploadFiles(IgCredentials igCredentials,
            String sourceFileRootPath, String hostingFolder)
            throws IgException {
        List<String> relativeFilePaths = findAllRelativeFilePaths(sourceFileRootPath);
        for (String relativePath : relativeFilePaths) {
            uploadFile(igCredentials, sourceFileRootPath, relativePath, hostingFolder);
        }
    }

    /**
     * Finds all relative file paths under given base folder.
     * These files will be uploaded to iGoogle server.
     */
    static List<String> findAllRelativeFilePaths(String baseFolder) {
        List<String> allPaths = new ArrayList<String>();
        findAllRelativeFilePaths(baseFolder, "", allPaths);
        return allPaths;
    }

    private static void findAllRelativeFilePaths(
            String baseFolder, String relativeFolder, List<String> allPaths) {
        // Assert that relativeFolder ends with "/" unless it is empty.
        assert ((relativeFolder.length() == 0)
                || (relativeFolder.charAt(relativeFolder.length() - 1) == '/'));

        File currentFolder = new File(baseFolder, relativeFolder);
        for (File file : currentFolder.listFiles()) {
            String relativeFilePath = relativeFolder + file.getName();
            if (file.isHidden()) {
                // Ignore any hidden file.
            } else if (file.isDirectory()) {
                findAllRelativeFilePaths(baseFolder, relativeFilePath + "/", allPaths);
            } else if (file.isFile()) {
                allPaths.add(relativeFilePath);
            } // Ignore all other kinds of files.
        }
    }

    static String retrieveQuotaByte(String sid, String publicId)
            throws IgException {
        String url = URL_IG_GADGETS_BYTE_QUOTA + publicId;
        String response = retrieveHttpResponseAsString(url, sid);
        return response;
    }

    static String retrieveQuotaByteUsed(String sid, String publicId)
            throws IgException {
        String url = URL_IG_GADGETS_BYTES_USED + publicId;
        String response = retrieveHttpResponseAsString(url, sid);
        return response;
    }

    /**
     * Retrieves list of file names under hosting folder.
     * Returns empty list if no file is found.
     *
     * @throws IgException
     */
    static List<String> retrieveFileNameList(String sid, String publicId, String hostingFolder)
            throws IgException {
        String url = URL_IG_GADGETS_DIRECTORY + publicId;
        String allFilesString = retrieveHttpResponseAsString(url, sid);
        List<String> allFileNameList = new ArrayList<String>();

        // Return empty List when no file is found.
        if (allFilesString.indexOf("<title>404 Not Found</title>") != -1) {
            return allFileNameList;
        }

        // Form List of files.
        String[] allFilesArray = allFilesString.split("\\n");
        // Sample of details of each file (namely, each line of returned content):
        // "39 text/plain; charset=UTF-8 dummy_host_folder/dummy_folder/dummy_file.xml"
        // So, make sure the hostingFolder should not start with "/".
        if (hostingFolder.startsWith("/")) {
            hostingFolder = hostingFolder.substring(1);
        }
        for (String file : allFilesArray) {
            String[] fileInfo = file.split(" ");
            String fileName = fileInfo[fileInfo.length - 1];
            if (fileName.startsWith(hostingFolder)) {
                allFileNameList.add(fileName);
            }
        }
        return allFileNameList;
    }

    /**
     * Returns the url for the hosted directory given hosting folder
     * and public id.
     *
     * @param publicId iGoogle public id
     * @param hostingFolder hosting folder
     * @return the url for the hosted directory
     */
    public static String formHostedDirectoryUrl(String publicId, String hostingFolder) {
        return URL_GMODULE_FILE + publicId + hostingFolder;
    }

    /**
     * Returns the url for the hosted file given hosting folder,
     * file path, and public id.
     *
     * @param publicId iGoogle public id
     * @param hostingFolder hosting folder
     * @param filePath path of the hosted file
     * @return the url for the hosted file
     */
    public static String formHostedFileUrl(
            String publicId, String hostingFolder, String filePath) {
        return formHostedDirectoryUrl(publicId, hostingFolder) + filePath;
    }

    /**
     * Sends HTTP request to iGoogle server and retrieves response
     * content as String.
     *
     * @return response as String
     * @throws IgException
     */
    static String retrieveHttpResponseAsString(String url, String sid)
            throws IgException {
        // Prepare HttpGet.
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "SID=" + sid);

        // Retrieve HttpResponse.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (ClientProtocolException e) {
            throw new IgException(e);
        } catch (IOException e) {
            throw new IgException(e);
        }
        logger.fine("status line: " + httpResponse.getStatusLine());
        return IgHttpUtil.retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
    }

    /**
     * Forms the url for previewing a legacy gadget.
     *
     * @return url for previewing
     */
    public static String formPreviewLegacyGadgetUrl(String hostedFileUrl, boolean useCanvasView) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_PREVIEW_LEGACY_GADGET);
        sb.append("&hl=en&gl=us&view=");
        sb.append(useCanvasView ? "canvas" : "home"); // default is home view

        // Append hosted file's url.
        sb.append("&url=");
        sb.append(hostedFileUrl);

        // TODO: Support various languages, and countries.

        return sb.toString();
    }

    /**
     * Forms url for previewing an OpenSocial gadget.
     *
     * @return url for previewing
     * @throws IgException
     */
    public static String formPreviewOpenSocialGadgetUrl(
            String hostedFileUrl, boolean useCanvasView, String sid)
            throws IgException {
        String requestUrl = URL_IG_PREVIEW_OPEN_SOCIAL_GADGET + hostedFileUrl;
        logger.fine("requestUrl: " + requestUrl);
        String response = retrieveHttpResponseAsString(requestUrl, sid);
        logger.fine("response:\n" + response);

        // Sample response (all in one line):
        // throw 1; < don't be evil' >{m:[{url:"http://.../gifts_1_friends.xml",
        // hifr:"http://8...c.ig.ig.gmodules.com/gadgets/ifr
        // ?view=home
        // &url=http://.../gifts_1_friends.xml
        // &nocache=0
        // &lang=en&country=us&.lang=en&.country=us
        // &synd=ig&mid=0&ifpctok=-3701245656769771047
        // &exp_split_js=1&exp_track_js=1&exp_new_js_flags=1&exp_ids=300213
        // &parent=http://www.google.com&refresh=3600
        // &libs=core:core.io:core.iglegacy:auth-refresh#st=c%3Dig%26e%3D...
        // &gadgetId=...&gadgetOwner=...&gadgetViewer=...
        // &is_signedin=1
        // &is_social=1",
        // cifr:"..."}]}

        // Form preview-url from response.
        // First, take out unwanted prepending string.
        String previewUrlIdentifier = useCanvasView ?
                PREVIEW_URL_IDENTIFIER_FOR_CANVAS :
                PREVIEW_URL_IDENTIFIER_FOR_HOME;
        int startIndex = response.indexOf(previewUrlIdentifier) + previewUrlIdentifier.length();
        String previewUrl = response.substring(startIndex);
        logger.fine("previewUrl with appending:\n" + previewUrl);

        // TODO: (p1) remove this if-block once the server's bug is fixed
        // This case is a bug from the igoogle server.
        // The bug: the returned value for canvas view is sometimes empty.
        // Here is the workaround.
        if (!previewUrl.startsWith("http://")) {
            if (!useCanvasView) {
                // If this happens, then it is another new bug from the igoogle server.
                throw new IgException("Error: Invalid preview url with response:\n" + response);
            }
            previewUrlIdentifier = PREVIEW_URL_IDENTIFIER_FOR_HOME;
            startIndex = response.indexOf(previewUrlIdentifier) + previewUrlIdentifier.length();
            previewUrl = response.substring(startIndex);
            logger.fine("fixed previewUrl with appending:\n" + previewUrl);
            previewUrl = previewUrl.replaceFirst("view=home", "view=canvas");
        }

        // Take out unwanted appending string.
        int endIndex = previewUrl.indexOf(
                PREVIEW_URL_END_IDENTIFIER) + PREVIEW_URL_END_IDENTIFIER_LENGTH;
        previewUrl = previewUrl.substring(0, endIndex);
        logger.fine("previewUrl without appending:\n" + previewUrl);

        // Always enable nocache.
        previewUrl = previewUrl.replaceFirst("&nocache=0&", "&nocache=1&");

        // TODO: support lang and country.

        return previewUrl;
    }

    /**
     * Forms the url for publishing a gadget.
     *
     * @return url for publishing
     */
    public static String formPublishGadgetUrl(String hostedFileUrl) {
        return URL_IG_SUBMIT_GADGET + hostedFileUrl;
    }

    /**
     * Deletes the hosted file.
     */
    public static void deleteFile(String fileName, IgCredentials igCredentials)
            throws IgException {
        // Make sure fileName starts with "/".
        if (!fileName.startsWith("/")) {
            fileName = "/" + fileName;
        }

        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + igCredentials.getPublicId() + fileName
                + "?synd=gde&action=delete&et=" + igCredentials.getEditToken();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/xml; charset=" + IgHttpUtil.ENCODING);
        // Do not set Content-Length header.

        // Add cookie headers. Cookie PREF must be placed before SID.
        httpPost.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "PREF=" + igCredentials.getPref());
        httpPost.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "SID=" + igCredentials.getSid());

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            throw new IgException(e);
        } catch (IOException e) {
            throw new IgException(e);
        }
        StatusLine statusLine = httpResponse.getStatusLine();

        // Verify result status.
        if (HttpStatus.SC_NO_CONTENT != statusLine.getStatusCode()) {
            String response =
                    IgHttpUtil.retrieveHttpResponseAsString(httpClient, httpPost, httpResponse);
            throw new IgException("Failed delete file with status line: "
                    + statusLine.toString() + "\nand response:\n" + response);
        }
    }

    /**
     * Cleans all files as hosted under the given hosting folder in iGoogle.
     *
     * @throws IgException
     */
    public static void cleanFiles(IgCredentials igCredentials, String hostingFolder)
            throws IgException {
        List<String> hostedFilesList =retrieveFileNameList(
                igCredentials.getSid(), igCredentials.getPublicId(), hostingFolder);
        logger.fine("file count: " + hostedFilesList.size());
        for (String file : hostedFilesList) {
            deleteFile(file, igCredentials);
        }
    }

    static void cleanFiles(String username, String password, String hostingFolder)
            throws IgException {
        IgCredentials igCredentials = new IgCredentials(username, password);

        // Clean files.
        IgHostingUtil.cleanFiles(igCredentials, hostingFolder);
    }

}