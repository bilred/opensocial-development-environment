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

import java.util.logging.Logger;

/**
 * Data and corresponding methods used for interacting with iGoogle service.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgPrefEditToken {
    private static Logger logger = Logger.getLogger(IgPrefEditToken.class.getName());

    private static final int MIN_PREF_LENGTH = 50;
    private static final int EDIT_TOKEN_LENGTH = 16;
    private static final String EDIT_TOKEN_IDENTIFIER = "?et=";
    private String pref;
    private String editToken;

    IgPrefEditToken(String pref, String editToken) {
        this.pref = pref;
        this.editToken = editToken;
    }

    static String retrieveEditTokenFromPageContent(String pageContent) throws HostingException {
        logger.info("pageContent: " + pageContent);
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

    static boolean validateEditToken(String editToken) {
        return (editToken != null)
                && (editToken.length() == EDIT_TOKEN_LENGTH);
    }

    public String toString() {
        return "pref: " + pref + ", editToken: " + editToken;
    }
}
