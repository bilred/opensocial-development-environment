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
import java.util.logging.Logger;

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

/**
 * This iGoogle utility class provides authentication and
 * gadget-hosting related methods to interact with iGoogle
 * (http://www.google.com/ig) gadget container.
 * <p>
 * Samples of usages could be found at
 * {@link HostingIGoogleUtilTest#testAllMethods()}.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class HostingIGoogleUtil {

    private static Logger logger = Logger.getLogger(HostingIGoogleUtil.class.getName());

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
     */
    static String retrieveSid(String emailUserName, String password,
        String loginCaptchaToken, String loginCaptchaAnswer) {

        // TODO: Can we get sid and Ig...Token all together?

        String response = null;
        try {
            response = requestAuthentication(
                emailUserName, password, loginCaptchaToken, loginCaptchaAnswer);
            logger.fine("response:\n" + " " + response);
        } catch (IOException e) {
            logger.severe("Error:\n" + e.getMessage());

            // TODO: Handle IOException
            return null;
        }

        // Parse the output
        response.trim();
        String [] tokens = response.split("\n");

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
     * Makes a HTTP POST request.
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
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

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
     * @return status line of HTTP response
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String uploadFile(String sid, String publicId, IgPrefEditToken prefEditToken,
        File sourceFile, String targetFilePath)
        throws ClientProtocolException, IOException {

        // Verify prefEditToken.
        String editToken = prefEditToken.getEditToken();
        if (editToken == null) {
            logger.warning("editToken is null");
            return "ERROR: editToken is null";
        }
        String pref = prefEditToken.getPref();
        if (pref == null) {
            logger.warning("pref is null");
            return "ERROR: pref is null";
        }

        // Prepare HttpPost.
        String url = URL_IG_GADGETS_FILE + publicId + "/" + targetFilePath + "?et=" + editToken;
        logger.fine("url: " + url);
        HttpPost httpPost = new HttpPost(url);
        FileEntity fileEntity = new FileEntity(sourceFile, "text/plain; charset=\"UTF-8\"");
        httpPost.setEntity(fileEntity);
        httpPost.setHeader("Content-Type", "text/plain");

        // Cookie PREF must be placed before SID.
        httpPost.addHeader("Cookie", "PREF=" + pref);
        httpPost.addHeader("Cookie", "SID=" + sid);

        // Execute request.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpPost);
        String statusLine = httpResponse.getStatusLine().toString();
        logger.info("statusLine: " + statusLine);
        String response = retrieveHttpResponseAsString(httpClient, httpPost, httpResponse);
        logger.fine("response: " + response);
        return statusLine;
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

        String url = URL_GMODULE_FILE + publicId + "/" + filePath;
        String response = sendHttpRequestToIg(url, sid);
        return response;
    }

    static String retrievePublicId(String sid)
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
        httpGet.setHeader("Content-Type", "text/plain");
        httpGet.addHeader("Cookie", "SID=" + sid);

        // Retrieve HttpResponse.
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        logger.info("status line: " + httpResponse.getStatusLine());
        return retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
    }

    static IgPrefEditToken retrieveIgPrefEditToken(String sid)
        throws ClientProtocolException, IOException {

        HttpGet httpGet = new HttpGet(URL_IG_PREF_EDIT_TOKEN);
        httpGet.setHeader("Content-Type", "text/plain");
        httpGet.addHeader("Cookie", "SID=" + sid);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        logger.info("status line: " + httpResponse.getStatusLine());

        String pref = null;
        for (Header header : httpResponse.getHeaders("Set-Cookie")) {
            String headerValue = header.getValue();
            if (headerValue.startsWith("PREF=ID=")) {
                // pref starts with "ID=" and ends before ";"
                pref = headerValue.substring(5, headerValue.indexOf(";"));
                logger.fine("Pref: " + pref);
                break;
            }
        }

        String responseString = retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
        String editToken = retrieveEditTokenFromPageContent(responseString);
        return new IgPrefEditToken(pref, editToken);
    }

    private static String retrieveEditTokenFromPageContent(String pageContent) {
        // Sample of edit token: et=ok49ZFcD (8 chars)
        int indexOfEditToken = pageContent.indexOf("?et=");

        // TODO: Check indexOfEditToken != -1

        // Retrieve the 8 chars after "?et=" (of which the length is 4)
        String editToken = pageContent.substring(indexOfEditToken + 4, indexOfEditToken + 4 + 8);
        logger.fine("editToken: " + editToken);
        return editToken;
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

    /**
     * Data used for interacting with iGoogle service.
     *
     * @author albert.cheng.ig@gmail.com
     */
    static class IgPrefEditToken {
        private String pref;
        private String editToken;

        private IgPrefEditToken(String pref, String editToken) {
            this.pref = pref;
            this.editToken = editToken;
        }

        String getPref() {
            return pref;
        }

        String getEditToken() {
            return editToken;
        }
    }
}
