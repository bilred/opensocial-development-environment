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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

/**
 * This iGoogle utility class provides authentication and
 * gadget-hosting related methods to interact with iGoogle
 * (http://www.google.com/ig) gadget container.
 * <p>
 * Samples of usages could be found at
 * {@link HostingIGoogleUtilTest#testAuthenticationAndUploadAndRetrieveFiles()}.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class HostingIGoogleUtil {

    private static Logger logger = Logger.getLogger(HostingIGoogleUtil.class.getName());

    private static final String OSDE_PREVIEW_DIRECTORY = "osde/preview/";
    private static final String HTTP_HEADER_COOKIE = "Cookie";
    private static final String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    private static final String HTTP_PLAIN_TEXT_TYPE_UTF8 =
        HTTP.PLAIN_TEXT_TYPE + HTTP.CHARSET_PARAM + HTTP.UTF_8;
    private static final String URL_GOOGLE_LOGIN = "https://www.google.com/accounts/ClientLogin";
    private static final String URL_IG_PREF_EDIT_TOKEN = "http://www.google.com/ig/resetprefs.html";
    private static final String URL_IG_GADGETS = "http://www.google.com/ig/gadgets";
    private static final String URL_IG_GADGETS_BYTE_QUOTA = URL_IG_GADGETS + "/bytequota/";
    private static final String URL_IG_GADGETS_BYTES_USED = URL_IG_GADGETS + "/bytesused/";
    private static final String URL_IG_GADGETS_DIRECTORY = URL_IG_GADGETS + "/directory/";
    private static final String URL_IG_GADGETS_FILE = URL_IG_GADGETS + "/file/";
    private static final String URL_GMODULE_FILE = "http://hosting.gmodules.com/ig/gadgets/file/";

    private HostingIGoogleUtil() {
        // Disable instantiation of this utility class.
    }

    /**
     * Retrieves the authentication SID token.
     *
     * @return the SID
     * @throws HostingException
     */
    public static String retrieveSid(String emailUserName, String password,
            String loginCaptchaToken, String loginCaptchaAnswer)
            throws HostingException {
        // TODO: Can we get sid and Ig...Token all together?

        String response = null;
        try {
            response = requestAuthentication(
                emailUserName, password, loginCaptchaToken, loginCaptchaAnswer);
            logger.fine("response:\n" + " " + response);
        } catch (IOException e) {
            throw new HostingException(e);
        }

        // Parse the output
        response.trim();
        String[] tokens = response.split("\n");

        // TODO: Refactor the following block of code to be more flexible.
        String sid = null;
        String errorMsg = null;
        for (String token : tokens) {
            if (token.startsWith("SID=")) {
                sid = token.substring(4); // "SID=".length = 4
            } else if (token.startsWith("Error=")) {
                errorMsg = token.substring(6); // "Error=".length= 6
                logger.severe("errorMsg: " + errorMsg);
            }
        }

        if (sid == null) {
            // TODO: Handle errors such as errorMsg="CaptchaRequired".
        }

        return sid;
    }

    /**
     * Makes a HTTP POST request for authentication.
     *
     * @param emailUserName the name of the user
     * @param password the password of the user
     * @param loginTokenOfCaptcha CAPTCHA token (Optional)
     * @param loginCaptchaAnswer answer of CAPTCHA token (Optional)
     * @return http response as a String
     * @throws IOException
     */
    private static String requestAuthentication(String emailUserName, String password,
            String loginTokenOfCaptcha, String loginCaptchaAnswer)
            throws IOException {
        // TODO: Refactor this method utilizing HttpPost.

        // Prepare connection.
        URL url = new URL(URL_GOOGLE_LOGIN);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // Form the POST params.
        StringBuilder params = new StringBuilder();
        params.append("Email=").append(emailUserName)
              .append("&Passwd=").append(password)
              .append("&source=OSDE-01&service=ig&accountType=HOSTED_OR_GOOGLE");
        if (loginTokenOfCaptcha != null) {
            params.append("&logintoken=").append(loginTokenOfCaptcha);
        }
        if (loginCaptchaAnswer != null) {
            params.append("&logincaptcha=").append(loginCaptchaAnswer);
        }

        // Send POST.
        OutputStream outputStream = null;
        try {
            outputStream = urlConnection.getOutputStream();
            outputStream.write(params.toString().getBytes("utf-8"));
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        // Retrieve response.
        // TODO: Should the caller of this method need to know the responseCode?
        int responseCode = urlConnection.getResponseCode();
        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                                  ? urlConnection.getInputStream()
                                  : urlConnection.getErrorStream();;
        try {
            String response = retrieveResponseStreamAsString(inputStream);
            return response;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * Uploads a file to iGoogle.
     *
     * @throws ClientProtocolException
     * @throws IOException
     * @throws HostingException
     */
    public static void uploadFile(String sid, String publicId, IgPrefEditToken prefEditToken,
            String sourceFileRootPath, String sourceFileRelativePath)
            throws ClientProtocolException, IOException, HostingException {
        // Validate prefEditToken.
        if (!prefEditToken.validate()) {
            throw new HostingException("Invalid prefEditToken: " + prefEditToken);
        }

        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + publicId + "/" + OSDE_PREVIEW_DIRECTORY
            + sourceFileRelativePath + "?et=" + prefEditToken.getEditToken();
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
        httpPost.addHeader(HTTP_HEADER_COOKIE, "PREF=" + prefEditToken.getPref());
        httpPost.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpPost);
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
     * @throws ClientProtocolException
     * @throws IOException
     * @throws HostingException
     */
    public static void uploadFiles(String sid, String publicId, IgPrefEditToken prefEditToken,
            String sourceFileRootPath)
            throws ClientProtocolException, IOException, HostingException {
        List<String> relativeFilePaths = findAllRelativeFilePaths(sourceFileRootPath);
        for (String relativePath : relativeFilePaths) {
            uploadFile(sid, publicId, prefEditToken, sourceFileRootPath, relativePath);
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
            throws ClientProtocolException, IOException {
        String url = URL_IG_GADGETS_BYTE_QUOTA + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrieveQuotaByteUsed(String sid, String publicId)
            throws ClientProtocolException, IOException {
        String url = URL_IG_GADGETS_BYTES_USED + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrieveFileList(String sid, String publicId)
            throws ClientProtocolException, IOException {
        String url = URL_IG_GADGETS_DIRECTORY + publicId;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrieveFile(String sid, String publicId, String filePath)
            throws ClientProtocolException, IOException {
        String url = formHostedFileUrl(publicId, filePath);
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    private static String formHostedFileUrl(String publicId, String filePath) {
        return URL_GMODULE_FILE + publicId + "/" + OSDE_PREVIEW_DIRECTORY + filePath;
    }

    public static String retrievePublicId(String sid)
            throws ClientProtocolException, IOException {
        String response = sendHttpRequestToIg(URL_IG_GADGETS, sid);
        return response;
    }

    /**
     * Sends HTTP request to iGoogle server.
     *
     * @return response as String
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static String sendHttpRequestToIg(String url, String sid)
            throws ClientProtocolException, IOException {
        // Prepare HttpGet.
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);

        // Retrieve HttpResponse.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        logger.fine("status line: " + httpResponse.getStatusLine());
        return retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
    }

    public static IgPrefEditToken retrieveIgPrefEditToken(String sid)
            throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(URL_IG_PREF_EDIT_TOKEN);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        logger.fine("status line: " + httpResponse.getStatusLine());

        String pref = null;
        for (Header header : httpResponse.getHeaders(HTTP_HEADER_SET_COOKIE)) {
            String headerValue = header.getValue();
            if (headerValue.startsWith("PREF=ID=")) {
                // pref starts with "ID=" and ends before ";"
                pref = headerValue.substring(5, headerValue.indexOf(";"));
                logger.fine("Pref: " + pref);
                break;
            }
        }

        String responseString = retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
        String editToken = IgPrefEditToken.retrieveEditTokenFromPageContent(responseString);
        return new IgPrefEditToken(pref, editToken);
    }

    private static String retrieveHttpResponseAsString(
            HttpClient httpClient, HttpRequestBase httpRequest, HttpResponse httpResponse)
            throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        String response = null;
        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
                response = retrieveResponseStreamAsString(inputStream);
            } catch (IOException e) {
                // The HttpClient's connection will be automatically released
                // back to the connection manager.
                logger.severe("Error:\n" + e.getMessage());
                throw e;
            } catch (RuntimeException e) { // To catch unchecked exception intentionally.
                // Abort HttpRequest in order to release HttpClient's connection
                // back to the connection manager.
                logger.severe("Error:\n" + e.getMessage());
                httpRequest.abort();
                throw e;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            httpClient.getConnectionManager().shutdown();
        }
        logger.fine("response: '" + response + "'");
        return response;
    }

    /**
     * Retrieves response stream as String.
     *
     * @param inputStream response stream which needs to be closed by
     *        the caller
     * @throws IOException
     */
    private static String retrieveResponseStreamAsString(InputStream inputStream)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.substring(0, sb.length() - 1); // ignore the last '\n'
    }

    public static String formPreviewGadgetUrl(
            String publicId, String filePath, boolean useCanvasView) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.gmodules.com/gadgets/ifr?&hl=en&gl=us&nocache=1&view=");
        sb.append(useCanvasView ? "canvas" : "home"); // default is home view

        // Append hosted file's url.
        sb.append("&url=");
        String hostedFileUrl = formHostedFileUrl(publicId, filePath);
        sb.append(hostedFileUrl);

        // TODO: Support various languages, and countries.

        return sb.toString();
    }
}