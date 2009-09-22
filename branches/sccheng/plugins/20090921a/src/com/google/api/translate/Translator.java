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
package com.google.api.translate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * The Translator class communicates with Google Translate service to do translations.
 * It supports the following functionalities as described in 
 * http://code.google.com/intl/zh-TW/apis/ajaxlanguage/documentation/reference.html
 * 1. Translate several strings, all from the same source language to the same target language.
 *    This is accomplished through the use of multiple q arguments.
 * 2. Translate one string into several languages. This is accomplished
 *    through the use of multiple langpair arguments.
 * 3. Translate a single string from one language to another
 * 
 * Sample usage:
 * <code>Translator translator = new Translator();</code>
 * <code>translator.translate("hello world", Language.ENGLISH, Language.FRENCH);</code>
 * 
 *    
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class Translator {
	
	private final static String GOOGLE_TRANSLATE_URL_PREFIX = 
		"http://ajax.googleapis.com/ajax/services/language/translate?v=1.0";
	
	private URLConnection connection;
	private String referrer = "";
	
	/**
	 * Translate a single string from one language to another.
	 * 
	 * @param text string to be translated
	 * @param from language of the original string
	 * @param to target language of the translation
	 * @return translated string in target language
	 */
	public synchronized String translate(String text, Language from, Language to) {
		StringBuilder builder = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
		constructQueryURL(builder, text, from, to);
		openConnection(builder.toString());

		// parse the response string into JSON object and return the desired result
		try {
			JSONObject json = new JSONObject(getJSONResponse());
			return json.getJSONObject("responseData").getString("translatedText");
		} catch (JSONException jse) {
			System.err.println("Can't parse the response from Google Translate service into JSON object");
			jse.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Translate a given string into different target languages
	 * (Batch interface for clients)
	 * 
	 * @param text string to be translated
	 * @param fromLanguage original language of text
	 * @param toLanguages target languages of translations of text
	 * @return translations of text in different languages
	 */
	public synchronized ArrayList<String> translate(String text, Language fromLanguage, Language... toLanguages) {
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
	 */
	public synchronized ArrayList<String> translate(Language fromLanguage, Language toLanguage, String... texts) {
		StringBuilder builder = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
		constructQueryURL(builder, fromLanguage, toLanguage, texts);
		openConnection(builder.toString());
		String response = getJSONResponse();
		return retrieveMultipleResultsFromJSONResponse(response);
	}
	
	/**
	 * Set the referrer who is using Google Translate API
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
	 */
	private void openConnection(String queryURL) {
		try {
			URL url = new URL(queryURL);
			connection = url.openConnection();
			connection.addRequestProperty("Referrer", referrer);
		} catch (MalformedURLException ex) {
			System.err.println("Invalid server url for Google Translate API");
			ex.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Can't establish connections to Google Translate service");
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Get response from Google Translate. This method assumes the connection is successfully established.
	 * Otherwise an IOException will be caught
	 * 
	 * @return response in String
	 */
	private String getJSONResponse() {
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException ioe) {
			System.err.println("Error fetching data from Google Translate API");
			ioe.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {
				System.err.println("Can't close connection to Google Translate API");
				ioe.printStackTrace();
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Retrieves multiple results if there are more than one translation results in the response.
	 * This method is called when the client uses batch interface for translation.
	 * 
	 * @param JSONResponse response from Google Translate
	 * @return A list of translations in String
	 */
	private ArrayList<String> retrieveMultipleResultsFromJSONResponse(String jsonResponse) {
		// parse the response using JSON libraries
		try {
			JSONObject json = new JSONObject(jsonResponse);
			JSONArray responseData = json.getJSONArray("responseData");
			ArrayList<String> results = new ArrayList<String>();
			for (int i = 0; i < responseData.length(); ++i) {
				results.add(responseData.getJSONObject(i).getJSONObject("responseData").getString("translatedText"));
			}
			return results;
		} catch (JSONException jse) {
			System.err.println("Can't parse the response from Google Translate service into JSON object");
			jse.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Encode the text to be translated and construct query parameter to be embedded into query URL
	 * 
	 * @param text string to be translated
	 * @return "&q=encoded_text"
	 */
	private void encodeAndConstructQueryText(StringBuilder builder, String text) {
		try {
			builder.append("&q=").append(URLEncoder.encode(text, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Error during encoding the text to be tranlated");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Construct language pair parameter for the query URL
	 * 
	 * @param fromLanguage
	 * @param toLanguage
	 * @return "&langpair=fromLanguage|toLanguage"
	 */
	private void constructLangPairQuery(StringBuilder builder, Language fromLanguage, Language toLanguage) {
		builder.append("&langpair=").append(fromLanguage.getLangCode());
		builder.append("%7C"); // %7C is the character "|" in the encoded url
		builder.append(toLanguage.getLangCode());
	}
	
	/**
	 * Construct 1:1..* translation query URL to be fed to Google Translate API
	 * 
	 * @param text string to be translated
	 * @param fromLanguage original language of text
	 * @param toLanguages target languages of translation
	 * @return Example: "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0
	 * &q=text&langpair=fromLanguage|toLanguages[0]&lanpair=fromLanguage|toLanguages[1]..."
	 */
	private void constructQueryURL(StringBuilder builder, String text, Language fromLanguage, Language... toLanguages) {
		encodeAndConstructQueryText(builder, text);
		for (Language toLanguage : toLanguages) {
			constructLangPairQuery(builder, fromLanguage, toLanguage);
		}
	}
	
	/**
	 * Constructs query URL for multiple string translations from fromLanguage to toLanguage
	 * 
	 * @param fromLanguage
	 * @param toLanguage
	 * @param texts
	 * @return Example: "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0
	 * &q=texts[0]&q=texts[1]&langpair=fromLanguage|toLanguage"
	 */
	private void constructQueryURL(StringBuilder builder, Language fromLanguage, Language toLanguage, String... texts) {
		for (String text : texts) {
			encodeAndConstructQueryText(builder, text);
		}
		constructLangPairQuery(builder, fromLanguage, toLanguage);
	}
}