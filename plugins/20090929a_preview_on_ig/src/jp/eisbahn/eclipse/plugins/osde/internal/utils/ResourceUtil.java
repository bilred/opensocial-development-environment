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
package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

/**
 * This class has common methods for operating a resource file.
 * @author Yoichiro Tanaka
 */
public class ResourceUtil {
    
    /**
     * Hide this constructor.
     */
    private ResourceUtil() {
    }
    
    /**
     * Retrieve the content in the resource file which you specify.
     * This method will use UTF-8 as the resource file encoding.
     * @param path The resource file path.
     * @return The Content.
     * @throws IOException When some errors occurred.
     */
    public static String loadTextResourceFile(String path) throws IOException {
        return loadTextResourceFile(path, "UTF-8");
    }

    /**
     * Retrieve the content in the resource file which you specify.
     * @param path The resource file path.
     * @param encoding File encoding.
     * @return The Content.
     * @throws IOException When some errors occurred.
     */
    public static String loadTextResourceFile(String path, String encoding) throws IOException {
        InputStreamReader in = null;
        StringWriter out = null;
        try {
            in = new InputStreamReader(ResourceUtil.class.getResourceAsStream(path), encoding);
            out = new StringWriter();
            IOUtils.copy(in, out);
            String result = out.toString();
            return result;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
