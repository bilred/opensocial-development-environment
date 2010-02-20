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
package com.googlecode.osde.internal.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.codec.binary.Base64;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 *
 */
public class MapUtil {

    static class MapUtilException extends Exception {
        private static final long serialVersionUID = -4755332862971142522L;

        public MapUtilException() {
            super();
        }

        public MapUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public MapUtilException(String message) {
            super(message);
        }

        public MapUtilException(Throwable cause) {
            super(cause);
        }
    }

    public static String toString(Map<String, String> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(data == null ? new HashMap<String, String>() : data);
        out.flush();
        byte[] bytes = baos.toByteArray();
        byte[] encoded = Base64.encodeBase64(bytes);
        return new String(encoded, "UTF-8");
    }

    public static Map<String, String> toMap(String data)
            throws IOException, ClassNotFoundException {

        if (StringUtils.isNotBlank(data)) {
            byte[] bytes = data.getBytes("UTF-8");
            byte[] decoded = Base64.decodeBase64(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
            ObjectInputStream in = new ObjectInputStream(bais);

            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) in.readObject();
            return result;
        }

        return new HashMap<String, String>();
    }
}
