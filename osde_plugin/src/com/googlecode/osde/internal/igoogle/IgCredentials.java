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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.googlecode.osde.internal.utils.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

/**
 * Data and corresponding methods used for interacting with iGoogle
 * credentials service.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgCredentials {
    private static Logger logger = new Logger(IgCredentials.class);

    private static final String URL_GOOGLE_LOGIN =
            IgHttpUtil.URL_HTTPS_GOOGLE + "accounts/ClientLogin";
    private static final String URL_IG_PREF_EDIT_TOKEN =
            IgHttpUtil.URL_HTTP_IG + "/resetprefs.html";

    private static final int SID_LENGTH = 203;
    private static final int PUBLIC_ID_LENGTH = 21;
    private static final int PREF_LENGTH = 66;
    private static final int EDIT_TOKEN_LENGTH = 16;
    private static final String EDIT_TOKEN_IDENTIFIER = "id=\"et\" value=\"";

    private final String sid;
    private final String publicId;
    private final String pref;
    private final String editToken;

    IgCredentials(String username, String password) throws IgException {
        // Retrieve sid.
        // TODO: Support captcha.
        sid = retrieveSid(username, password, null, null);
        validateSid();

        // Retrieve publidId.
        publicId = retrievePublicId(sid);
        validatePublicId();

        // Prepare HttpGet for retrieving pref and editToken.
        HttpGet httpGet = new HttpGet(URL_IG_PREF_EDIT_TOKEN);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(IgHttpUtil.HTTP_HEADER_COOKIE, "SID=" + sid);
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

        // Retrieve pref from headers.
        pref = retrievePref(httpResponse);
        validatePref();

        // Retrieve editToken from response content.
        editToken = retrieveEditToken(httpClient, httpGet, httpResponse);
        validateEditToken();
    }

    /**
     * Retrieves the authentication SID token.
     *
     * @return the SID
     * @throws IgException
     */
    public static String retrieveSid(String emailUserName, String password,
            String loginCaptchaToken, String loginCaptchaAnswer)
            throws IgException {
        // TODO: Can we get sid and Ig...Token all together?

        String response = null;
        try {
            response = requestAuthentication(
                    emailUserName, password, loginCaptchaToken, loginCaptchaAnswer);
            logger.fine("response:\n" + " " + response);
        } catch (IOException e) {
            throw new IgException(e);
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
                logger.error("errorMsg: " + errorMsg);
                // TODO: Handle errors such as errorMsg="CaptchaRequired".
                throw new IgException("Failed authentication with Error= " + errorMsg);
            }
        }

        if (sid == null) {
            throw new IgException("No SID returned from the iGoogle server");
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
        logger.fine("url: " + url);

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

        // Send POST via output stream.
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
        logger.fine("responseCode: " + responseCode);
        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? urlConnection.getInputStream()
                : urlConnection.getErrorStream();
        ;
        logger.fine("inputStream: " + inputStream);
        try {
            // TODO: is there any constant for "UTF-8"?
            String response = IOUtils.toString(inputStream, "UTF-8");
            return response;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * Retrieves iGoogle public id by providing SID.
     */
    public static String retrievePublicId(String sid)
            throws IgException {
        String response =
                IgHostingUtil.retrieveHttpResponseAsString(IgHttpUtil.URL_IG_GADGETS, sid);
        return response;
    }

    private static String retrievePref(HttpResponse httpResponse) {
        String pref = null;
        for (Header header : httpResponse.getHeaders(IgHttpUtil.HTTP_HEADER_SET_COOKIE)) {
            String headerValue = header.getValue();
            if (headerValue.startsWith("PREF=ID=")) {
                // pref starts with "ID=" and ends before ";"
                pref = headerValue.substring(5, headerValue.indexOf(";"));
                break;
            }
        }
        return pref;
    }

    private static String retrieveEditToken(
            HttpClient httpClient, HttpGet httpGet, HttpResponse httpResponse)
            throws IgException {
        String pageContent =
                IgHttpUtil.retrieveHttpResponseAsString(httpClient, httpGet, httpResponse);
        int startIndexOfEditTokenIdentifier = pageContent.indexOf(EDIT_TOKEN_IDENTIFIER);
        if (startIndexOfEditTokenIdentifier == -1) {
            throw new IgException("Invalid editToken with pageContent:\n" + pageContent);
        }
        int startIndexOfEditTokenValue =
                startIndexOfEditTokenIdentifier + EDIT_TOKEN_IDENTIFIER.length();
        String editToken = pageContent.substring(startIndexOfEditTokenValue,
                startIndexOfEditTokenValue + EDIT_TOKEN_LENGTH);
        return editToken;
    }

    String getSid() {
        return sid;
    }

    String getPublicId() {
        return publicId;
    }

    String getPref() {
        return pref;
    }

    String getEditToken() {
        return editToken;
    }

    private boolean validateSid() {
        return (sid != null)
                && (sid.length() == SID_LENGTH);
    }

    private boolean validatePublicId() {
        return (publicId != null)
                && (publicId.length() == PUBLIC_ID_LENGTH);
    }

    private boolean validatePref() {
        return (pref != null)
                && (pref.length() == PREF_LENGTH);
    }

    private boolean validateEditToken() {
        return (editToken != null)
                && (editToken.length() == EDIT_TOKEN_LENGTH);
    }

    public String toString() {
        return new StringBuilder()
                .append("sid: ").append(sid)
                .append("\npublicId: ").append(publicId)
                .append("\npref: ").append(pref)
                .append("\neditToken: ").append(editToken)
                .append("\n").toString();
    }
}
