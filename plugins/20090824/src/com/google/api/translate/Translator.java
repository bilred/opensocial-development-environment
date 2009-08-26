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
 * 3. Translate several strings, each into a different language. In this case,
 *    there must be the same number of q arguments as there are langpair arguments,
 *    with them paired up in order
 *    
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class Translator {
	
	private static final String GOOGLE_TRANSLATE_URL_PREFIX = 
		"http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q=";
	
	/**
	 * Translate a single string from one language to another
	 * 
	 * @param text string to be translated
	 * @param from language of the original string
	 * @param to target language of the translation
	 * @return translated string in target language
	 */
	public static String translate(String text, Language from, Language to) {
		try {
			String queryURL = constructQueryURL(text, from, to);
			URL url = new URL(queryURL);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", "http://code.google.com/p/opensocial-development-environment");
			
			// read in the response from google translate and construct a string to store it
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
			 builder.append(line);
			}

			// parse the response string into JSON object and return the desired result
			JSONObject json = new JSONObject(builder.toString());
			String result = json.getJSONObject("responseData").getString("translatedText"); 
			return result;
		} catch (MalformedURLException ex) {
			System.err.println("Invalid server url for Google Translate API");
		} catch (IOException ioe) {
			System.err.println("Can't establish connections to Google Translate service");
		} catch (JSONException jse) {
			System.err.println("Can't parse the response from Google Translate service into JSON object");
		}
		return null;
	}
	
	private static String constructQueryURL(String text, Language from, Language to) {
		StringBuilder result = new StringBuilder(GOOGLE_TRANSLATE_URL_PREFIX);
		try {
			result.append(URLEncoder.encode(text, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Error during encoding the text to be tranlated.");
		}
		result.append("&langpair=");
		result.append(from.getLanguageCode());
		result.append("%7C");
		result.append(to.getLanguageCode());

		return result.toString();
	}
}