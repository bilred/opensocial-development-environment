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

/**
 * Data and corresponding methods used for interacting with iGoogle
 * credentials service.
 *
 * TODO: Move other credentials info (such SID, PREF cookies, etc) and
 * utility here.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgCredentials {
    private static final int MIN_PREF_LENGTH = 50;
    private static final int EDIT_TOKEN_LENGTH = 16;
    private static final String EDIT_TOKEN_IDENTIFIER = "id=\"et\" value=\"";
    private String pref;
    private String editToken;

    IgCredentials(String pref, String editToken) {
        this.pref = pref;
        this.editToken = editToken;
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
