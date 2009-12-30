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
package com.googlecode.osde.internal.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import static com.googlecode.osde.internal.utils.IgCredentials.HTTP_HEADER_COOKIE;
import static com.googlecode.osde.internal.utils.IgCredentials.URL_IG;
import static com.googlecode.osde.internal.utils.IgCredentials.retrieveHttpResponseAsString;

/**
 * This iGoogle utility class provides authentication and
 * gadget-hosting related methods to interact with iGoogle
 * (http://www.google.com/ig) gadget container.
 * <p>
 * Samples of usages could be found at
 * {@link HostingIGoogleUtilTest#testAuthenticationAndUploadAndRetrieveAndDeleteFiles()}.
 *
 * @author albert.cheng.ig@gmail.com
 */
// TODO: Rename HostingIGoogleUtil to IgHostingUtil and move it to production package.
public class HostingIGoogleUtil {

    private static Logger logger = new Logger(HostingIGoogleUtil.class);

    private static final String HTTP_PLAIN_TEXT_TYPE_UTF8 =
            HTTP.PLAIN_TEXT_TYPE + HTTP.CHARSET_PARAM + HTTP.UTF_8;

    private static final String URL_IG_GADGETS = URL_IG + "/gadgets";
    private static final String URL_IG_GADGETS_BYTE_QUOTA = URL_IG_GADGETS + "/bytequota/";
    private static final String URL_IG_GADGETS_BYTES_USED = URL_IG_GADGETS + "/bytesused/";
    private static final String URL_IG_GADGETS_DIRECTORY = URL_IG_GADGETS + "/directory/";
    private static final String URL_IG_GADGETS_FILE = URL_IG_GADGETS + "/file/";
    private static final String URL_IG_PREVIEW_OPEN_SOCIAL_GADGET =
            URL_IG + "/iframeurl?moduleurl=";
    private static final String URL_IG_SUBMIT_GADGET = URL_IG + "/submit?prefill_url=";

    private static final String URL_GMODULE = "http://hosting.gmodules.com/";
    private static final String URL_GMODULE_FILE = URL_GMODULE + "ig/gadgets/file/";
    private static final String URL_PREVIEW_LEGACY_GADGET = URL_GMODULE + "gadgets/ifr?nocache=1";

    private HostingIGoogleUtil() {
        // Disable instantiation of this utility class.
    }

    /**
     * Uploads a file to iGoogle.
     *
     * @throws HostingException
     */
    public static void uploadFile(String sid, String publicId, IgCredentials igCredentials,
            String sourceFileRootPath, String sourceFileRelativePath, String hostingFolder)
            throws HostingException {
        // Validate igCredentials.
        if (!igCredentials.validate()) {
            throw new HostingException("Invalid igCredentials: " + igCredentials);
        }

        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + publicId + hostingFolder
            + sourceFileRelativePath + "?et=" + igCredentials.getEditToken();
        logger.fine("url: " + url);
        HttpPost httpPost = new HttpPost(url);
        File sourceFile = new File(sourceFileRootPath, sourceFileRelativePath);
        String httpContentType = isTextExtensionForGadgetFile(sourceFileRelativePath)
                ? HTTP_PLAIN_TEXT_TYPE_UTF8
                : HTTP.DEFAULT_CONTENT_TYPE;
        httpPost.setHeader(HTTP.CONTENT_TYPE, httpContentType);
        FileEntity fileEntity = new FileEntity(sourceFile, httpContentType);
        logger.fine("fileEntity length: " + fileEntity.getContentLength());
        httpPost.setEntity(fileEntity);

        // Add cookie headers. Cookie PREF must be placed before SID.
        httpPost.addHeader(HTTP_HEADER_COOKIE, "PREF=" + igCredentials.getPref());
        httpPost.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            throw new HostingException(e);
        } catch (IOException e) {
            throw new HostingException(e);
        }
        StatusLine statusLine = httpResponse.getStatusLine();
        logger.fine("statusLine: " + statusLine);
        if (HttpStatus.SC_CREATED != statusLine.getStatusCode()) {
            String response = retrieveHttpResponseAsString(httpClient, httpPost, httpResponse);
            throw new HostingException("Failed file-upload with status line: "
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
     * @throws HostingException
     */
    public static void uploadFiles(String sid, String publicId, IgCredentials igCredentials,
            String sourceFileRootPath, String hostingFolder)
            throws HostingException {
        List<String> relativeFilePaths = findAllRelativeFilePaths(sourceFileRootPath);
        for (String relativePath : relativeFilePaths) {
            uploadFile(sid, publicId, igCredentials, sourceFileRootPath, relativePath,
                    hostingFolder);
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
        assert((relativeFolder.length() == 0)
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
            throws HostingException {
        String url = URL_IG_GADGETS_BYTE_QUOTA + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrieveQuotaByteUsed(String sid, String publicId)
            throws HostingException {
        String url = URL_IG_GADGETS_BYTES_USED + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrieveFileList(String sid, String publicId)
            throws HostingException {
        String url = URL_IG_GADGETS_DIRECTORY + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
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
        return URL_GMODULE_FILE + publicId + hostingFolder + filePath;
    }

    /**
     * Retrieves iGoogle public id by providing SID.
     */
    public static String retrievePublicId(String sid)
            throws HostingException {
        String response = sendHttpRequestToIg(URL_IG_GADGETS, sid);
        return response;
    }

    /**
     * Sends HTTP request to iGoogle server.
     *
     * @return response as String
     * @throws HostingException
     */
    static String sendHttpRequestToIg(String url, String sid)
            throws HostingException {
        // Prepare HttpGet.
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);

        // Retrieve HttpResponse.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (ClientProtocolException e) {
            throw new HostingException(e);
        } catch (IOException e) {
            throw new HostingException(e);
        }
        logger.fine("status line: " + httpResponse.getStatusLine());
        return retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
    }

    /**
     * Forms the url for previewing a legacy gadget.
     *
     * @return url for previewing
     */
    public static String formPreviewLegacyGadgetUrl(
            String hostedFileUrl, boolean useCanvasView) {
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
     * @throws HostingException
     */
    // TODO (p1): Utilize formPreviewOpenSocialGadgetUrl() when server is ready.
    public static String formPreviewOpenSocialGadgetUrl(
            String hostedFileUrl, boolean useCanvasView, String sid)
            throws HostingException {
        String requestUrl = URL_IG_PREVIEW_OPEN_SOCIAL_GADGET + hostedFileUrl;
        logger.fine("requestUrl: " + requestUrl);
        String response = sendHttpRequestToIg(requestUrl, sid);
        return response;
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
    public static void deleteFile(String sid, String publicId, String fileName,
            IgCredentials igCredentials)
            throws HostingException {
        // Validate igCredentials.
        if (!igCredentials.validate()) {
            throw new HostingException("Invalid igCredentials: " + igCredentials);
        }

        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + publicId + fileName
                + "?synd=gde&action=delete&et=" + igCredentials.getEditToken();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/xml; charset=UTF-8");
        // Do not set Content-Length header.

        // Add cookie headers. Cookie PREF must be placed before SID.
        httpPost.addHeader(HTTP_HEADER_COOKIE, "PREF=" + igCredentials.getPref());
        httpPost.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            throw new HostingException(e);
        } catch (IOException e) {
            throw new HostingException(e);
        }
        StatusLine statusLine = httpResponse.getStatusLine();

        // Verify result status.
        if (HttpStatus.SC_NO_CONTENT != statusLine.getStatusCode()) {
            String response = retrieveHttpResponseAsString(httpClient, httpPost, httpResponse);
            throw new HostingException("Failed delete file with status line: "
                    + statusLine.toString() + "\nand response:\n" + response);
        }
    }

    public static void cleanFiles(String sid, String publicId, IgCredentials igCredentials,
            String hostingFolder) {
        // TODO: (p0) Implement cleanFiles()
    }
}