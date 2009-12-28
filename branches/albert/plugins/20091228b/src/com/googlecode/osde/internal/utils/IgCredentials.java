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

import java.io.BufferedReader;
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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

/**
 * Data and corresponding methods used for interacting with iGoogle
 * credentials service.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgCredentials {
    private static Logger logger = Logger.getLogger(IgCredentials.class.getName());

    static final String HTTP_HEADER_COOKIE = "Cookie";
    private static final String HTTP_HEADER_SET_COOKIE = "Set-Cookie";
    private static final String URL_GOOGLE_LOGIN = "https://www.google.com/accounts/ClientLogin";
    private static final String URL_IG_PREF_EDIT_TOKEN = "http://www.google.com/ig/resetprefs.html";
    private static final int MIN_PREF_LENGTH = 50;
    private static final int EDIT_TOKEN_LENGTH = 16;
    private static final String EDIT_TOKEN_IDENTIFIER = "id=\"et\" value=\"";

    private String pref;
    private String editToken;

    IgCredentials(String pref, String editToken) {
        this.pref = pref;
        this.editToken = editToken;
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
     * Retrieves response stream as String.
     *
     * @param inputStream response stream which needs to be closed by
     *        the caller
     * @throws IOException
     */
    static String retrieveResponseStreamAsString(InputStream inputStream)
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
     * Retrieves iGoogle credentials.
     */
    public static IgCredentials retrieveIgCredentials(String sid)
            throws HostingException {
        HttpGet httpGet = new HttpGet(URL_IG_PREF_EDIT_TOKEN);
        httpGet.setHeader(HTTP.CONTENT_TYPE, HTTP.PLAIN_TEXT_TYPE);
        httpGet.addHeader(HTTP_HEADER_COOKIE, "SID=" + sid);
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
        String editToken = IgCredentials.retrieveEditTokenFromPageContent(responseString);
        return new IgCredentials(pref, editToken);
    }

    static String retrieveHttpResponseAsString(
            HttpClient httpClient, HttpRequestBase httpRequest, HttpResponse httpResponse)
            throws HostingException {
        HttpEntity entity = httpResponse.getEntity();
        String response = null;
        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
                response = IgCredentials.retrieveResponseStreamAsString(inputStream);
            } catch (IOException e) {
                // The HttpClient's connection will be automatically released
                // back to the connection manager.
                logger.severe("Error:\n" + e.getMessage());
                throw new HostingException(e);
            } catch (RuntimeException e) { // To catch unchecked exception intentionally.
                // Abort HttpRequest in order to release HttpClient's connection
                // back to the connection manager.
                logger.severe("Error:\n" + e.getMessage());
                httpRequest.abort();
                throw new HostingException(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new HostingException(e);
                    }
                }
            }
            httpClient.getConnectionManager().shutdown();
        }
        logger.fine("response: '" + response + "'");
        return response;
    }

    static String retrieveEditTokenFromPageContent(String pageContent) throws HostingException {
        int startIndexOfEditTokenIdentifier = pageContent.indexOf(EDIT_TOKEN_IDENTIFIER);

        if (startIndexOfEditTokenIdentifier == -1) {
            return null;
        }

        int startIndexOfEditTokenValue =
                startIndexOfEditTokenIdentifier + EDIT_TOKEN_IDENTIFIER.length();
        String editToken = pageContent.substring(startIndexOfEditTokenValue,
                startIndexOfEditTokenValue + EDIT_TOKEN_LENGTH);
        if (!validateEditToken(editToken)) {
            throw new HostingException("Invalid editToken: '" + editToken + "'\n"
                    + "with pageContent:\n" + pageContent);
        }
        return editToken;
    }

    private static boolean validateEditToken(String editToken) {
        return (editToken != null)
                && (editToken.length() == EDIT_TOKEN_LENGTH);
    }

    String getPref() {
        return pref;
    }

    String getEditToken() {
        return editToken;
    }

    boolean validate() {
        return validatePref()
                && validateEditToken();
    }

    private boolean validatePref() {
        return (pref != null)
            && (pref.length() > MIN_PREF_LENGTH);
    }

    private boolean validateEditToken() {
        return validateEditToken(editToken);
    }

    public String toString() {
        return "pref: " + pref + ", editToken: " + editToken;
    }
}
