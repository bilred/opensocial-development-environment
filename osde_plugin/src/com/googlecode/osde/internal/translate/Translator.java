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
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * The Translator class communicates with Google Translate service to do translations.
 * It supports the following functionalities as described in
 * http://code.google.com/intl/zh-TW/apis/ajaxlanguage/documentation/reference.html
 * 1. Translate several strings, all from the same source language to the same target language.
 * This is accomplished through the use of multiple q arguments.
 * 2. Translate one string into several languages. This is accomplished
 * through the use of multiple langpair arguments.
 * 3. Translate a single string from one language to another.
 *
 * Sample usage:
 * <code>Translator translator = new Translator();</code>
 * <code>translator.translate("hello world", Language.ENGLISH, Language.FRENCH);</code>
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public class Translator {

    protected final static String GOOGLE_TRANSLATE_URL_PREFIX =
            "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0";

    protected URLConnection connection;
    protected String referrer = "";

    /**
     * Translate a single string from one language to another.
     *
     * @param text string to be translated
     * @param from language of the original string
     * @param to target language of the translation
     * @return translated string in target language
     * @throws Exception
     */
    public String translate(String text, Language from, Language to)
            throws Exception {
        StringBuilder builder = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
        constructQueryURL(builder, text, from, to);
        openConnection(builder.toString());

        // parse the response string into JSON object and return the desired result
        return retrieveSingleResultFromJSONResponse(getJSONResponse());
    }

    /**
     * Translate a given string into different target languages
     * (Batch interface for clients)
     *
     * @param text string to be translated
     * @param fromLanguage original language of text
     * @param toLanguages target languages of translations of text
     * @return translations of text in different languages
     * @throws Exception
     */
    public ArrayList<String> translate(String text, Language fromLanguage, Language... toLanguages)
            throws Exception {
        StringBuilder builder = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
        constructQueryURL(builder, text, fromLanguage, toLanguages);
        openConnection(builder.toString());
        String response = getJSONResponse();
        return retrieveMultipleResultsFromJSONResponse(response);
    }

    /**
     * Translate multiple strings from one language to another in batch
     * (Batch interface for clients)
     *
     * @param fromLanguage
     * @param toLanguage
     * @param texts
     * @return translations for every string in texts from fromLanguage to toLanguage
     * @throws Exception
     */
    public ArrayList<String> translate(Language fromLanguage, Language toLanguage, String... texts)
            throws Exception {
        StringBuilder builder = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
        constructQueryURL(builder, fromLanguage, toLanguage, texts);
        openConnection(builder.toString());
        String response = getJSONResponse();
        return retrieveMultipleResultsFromJSONResponse(response);
    }

    /**
     * Set the referrer who is using Google Translate API
     *
     * @param referrer
     */
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getReferrer() {
        return this.referrer;
    }

    /**
     * Opens connection and set referrer for the connection
     *
     * @param queryURL
     * @throws IOException
     */
    protected void openConnection(String queryURL) throws IOException {
        try {
            URL url = new URL(queryURL);
            connection = url.openConnection();
            connection.addRequestProperty("Referrer", referrer);
        } catch (IOException ioe) {
            System.err.println("Can't establish connections to Google Translate service");
            throw ioe;
        }
    }

    /**
     * Closes connection to Google Translate service
     *
     * @param reader
     * @throws IOException
     */
    protected void closeConnection(BufferedReader reader) throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    /**
     * Get response from Google Translate. This method assumes the connection is successfully established.
     * Otherwise an IOException will be caught
     *
     * @return Response in String
     * @throws IOException
     */
    protected String getJSONResponse() throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException ioe) {
            System.err.println("Error fetching data from Google Translate API");
            throw ioe;
        } finally {
            closeConnection(reader);
        }

        return builder.toString();
    }

    /**
     * @param jsonResponse Response to parsed and from which to retrieve translation result
     * @return translation result
     * @throws JSONException
     */
    protected String retrieveSingleResultFromJSONResponse(String jsonResponse)
            throws Exception {
        try {
            JSONObject json = (JSONObject) JSONValue.parse(jsonResponse);
            return (String) ((JSONObject)json.get("responseData")).get("translatedText");
        } catch (Exception e) {
            System.err.println(
                    "Can't parse the response from Google Translate service into JSON object");
            throw e;
        }
    }

    /**
     * Retrieves multiple results if there are more than one translation results in the response.
     * This method is called when the client uses batch interface for translation.
     *
     * @param jsonResponse response from Google Translate
     * @return A list of translations in String
     * @throws JSONException
     */
    protected ArrayList<String> retrieveMultipleResultsFromJSONResponse(String jsonResponse)
            throws Exception {
            try {
                JSONObject json = (JSONObject) JSONValue.parse(jsonResponse);
                JSONArray responseData = (JSONArray) json.get("responseData");
                ArrayList<String> results = new ArrayList<String>();
                for (Object o : responseData) {
                    JSONObject obj = (JSONObject) o;
                    results.add((String) ((JSONObject)obj.get("responseData")).get("translatedText"));
                }
                return results;
            } catch (Exception e) {
                System.err.println(
                "Can't parse the response from Google Translate service into JSON object");
                throw e;
            }
    }

    /**
     * Encodes the text to be translated and construct query
     * parameter to be embedded into query URL.
     * <p>
     * Appends &quot;&amp;q=encoded_text&quot; in the parameter builder.
     *
     * @throws UnsupportedEncodingException
     */
    protected void encodeAndConstructQueryText(
            StringBuilder builder, String sourceText, String encoding)
            throws UnsupportedEncodingException {
        try {
            builder.append("&q=").append(URLEncoder.encode(sourceText, encoding));
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Error during encoding the text to be tranlated");
            throw ex;
        }
    }

    /**
     * Constructs language pair parameter for the query URL.
     * <p>
     * Appends &quot;&amp;langpair=fromLanguage|toLanguage&quot; in builder.
     */
    protected void constructLangPairQuery(StringBuilder builder, Language fromLanguage,
            Language toLanguage) {
        builder.append("&langpair=").append(fromLanguage.getLangCode());
        builder.append("%7C"); // %7C is the character "|" in the encoded url
        builder.append(toLanguage.getLangCode());
    }

    /**
     * Constructs 1:1..* translation query URL to be fed to Google Translate API.
     * <p>
     * Example appending to builder:<br>
     * &quot;http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&amp;q=text&amp;langpair=fromLanguage|toLanguages[0]&amp;lanpair=fromLanguage|toLanguages[1]...&quot;
     *
     * @throws UnsupportedEncodingException
     */
    protected void constructQueryURL(StringBuilder builder, String sourceText,
            Language fromLanguage, Language... toLanguages)
            throws UnsupportedEncodingException {
        encodeAndConstructQueryText(builder, sourceText, "UTF-8");
        for (Language toLanguage : toLanguages) {
            constructLangPairQuery(builder, fromLanguage, toLanguage);
        }
    }

    /**
     * Constructs query URL for multiple string translations from fromLanguage to toLanguage.
     * <p>
     * Example appending to builder:<br>
     * &quot;http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&amp;q=texts[0]&amp;q=texts[1]&amp;langpair=fromLanguage|toLanguage&quot;
     *
     * @throws UnsupportedEncodingException
     */
    protected void constructQueryURL(StringBuilder builder, Language fromLanguage,
            Language toLanguage, String... texts) throws UnsupportedEncodingException {
        for (String text : texts) {
            encodeAndConstructQueryText(builder, text, "UTF-8");
        }
        constructLangPairQuery(builder, fromLanguage, toLanguage);
    }
}
