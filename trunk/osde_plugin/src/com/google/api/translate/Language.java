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

/**
 * Defines Language enum with languages that are supported by Google Translate API
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public enum Language {
    AFRIKAANS("af"),
    ALBANIAN("sq"),
    AMHARIC("am"),
    ARABIC("ar"),
    ARMENIAN("hy"),
    AZERBAIJANI("az"),
    BASQUE("eu"),
    BELARUSIAN("be"),
    BENGALI("bn"),
    BIHARI("bh"),
    BULGARIAN("bg"),
    BURMESE("my"),
    CATALAN("ca"),
    CHEROKEE("chr"),
    CHINESE("zh"),
    CHINESE_SIMPLIFIED("zh-CN"),
    CHINESE_TRADITIONAL("zh-TW"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DHIVEHI("dv"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESPERANTO("eo"),
    ESTONIAN("et"),
    FILIPINO("tl"),
    FINNISH("fi"),
    FRENCH("fr"),
    GALICIAN("gl"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    GUARANI("gn"),
    GUJARATI("gu"),
    HEBREW("iw"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    INDONESIAN("id"),
    INUKTITUT("iu"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KANNADA("kn"),
    KAZAKH("kk"),
    KHMER("km"),
    KOREAN("ko"),
    KURDISH("ku"),
    KYRGYZ("ky"),
    LAOTHIAN("lo"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    MALAY("ms"),
    MALAYALAM("ml"),
    MALTESE("mt"),
    MARATHI("mr"),
    MONGOLIAN("mn"),
    NEPALI("ne"),
    NORWEGIAN("no"),
    ORIYA("or"),
    PASHTO("ps"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt-PT"),
    PUNJABI("pa"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SANSKRIT("sa"),
    SERBIAN("sr"),
    SINDHI("sd"),
    SINHALESE("si"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAJIK("tg"),
    TAMIL("ta"),
    TAGALOG("tl"),
    TELUGU("te"),
    THAI("th"),
    TIBETAN("bo"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    UZBEK("uz"),
    UIGHUR("ug"),
    VIETNAMESE("vi"),
    WELSH("cy"),
    YIDDISH("yi");

    private final String langCode;

    private Language(String langCode) {
        this.langCode = langCode;
    }

    /**
     * Determines if the given language is supported in Google Translate API
     *
     * @param langCode incoming query of language code
     * @return true if langCode is supported in Google Translate API
     */
    public static boolean isSupported(String langCode) {
        String lowerLangCode = langCode.toLowerCase();
        for (Language language : values()) {
            if (lowerLangCode.equals(language.langCode.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Language object given a language code, null otherwise
     * The logic in this method is similar to isSupported() method,
     * except that isSupported() returns true or false to indicate if a given language
     * code is supported. And this method returns explicitly a Language object or null.
     *
     * @param langCode a String that represents the language code
     * @return Language object of the language code
     */
    public static Language fromString(String langCode) {
        String lowerLangCode = langCode.toLowerCase();
        for (Language language : values()) {
            if (lowerLangCode.equals(language.langCode.toLowerCase())) {
                return language;
            }
        }
        return null;
    }

    /**
     * Returns the language code of the language
     *
     * @return language code of this language
     */
    public String getLangCode() {
		return this.langCode;
	}
}
