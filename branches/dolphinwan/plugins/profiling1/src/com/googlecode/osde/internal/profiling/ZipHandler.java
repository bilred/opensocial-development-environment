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

package com.googlecode.osde.internal.profiling;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

/**
 * Zip file/stream handler.
 * Note that it is a simplified version of WebDriver's TemporaryFileSystem and
 * FileHandler.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class ZipHandler {
    private static final int BUF_SIZE = 16384;
    private static final File baseDir = new File(System.getProperty("java.io.tmpdir"));
    private final Set<File> temporaryFiles = new CopyOnWriteArraySet<File>();

    /**
     * Returns true if a given file name is probably a zip file.
     */
    public boolean isZipped(String fileName) {
        return fileName.endsWith(".zip") || fileName.endsWith(".xpi");
    }

    /**
     * Unzips a zip content into a physical folder.
     * @return The newly-created folder with the unzipped content.
     */
    public File unzip(InputStream resource) throws IOException {
        File output = createTempDir();

        ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(resource, BUF_SIZE));
        ZipEntry entry = zipStream.getNextEntry();
        while (entry != null) {
            final File target = new File(output, entry.getName());
            if (entry.isDirectory()) {
                forceMkdir(target);
            } else {
                unzipFile(target, zipStream);
            }
            entry = zipStream.getNextEntry();
        }

        return output;
    }

    private void unzipFile(File target, InputStream zipStream)
            throws IOException {
        FileOutputStream out = null;
        try {
            forceMkdir(target.getParentFile());
            out = new FileOutputStream(target);
            copy(zipStream, out);
        } finally {
            closeQuietly(out);
        }
    }

    public void cleanTemporaryFiles() {
        for (File file : temporaryFiles) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                // nothing could be done here as we are shutting down.
            }
        }
    }

    /**
     * Create a temporary directory, and track it for deletion.
     *
     * @return the temporary directory to create
     */
    private File createTempDir() {
        try {
            // Create a tempfile, and delete it.
            File file = File.createTempFile("unzip", "stream", baseDir);
            file.delete();

            // Create it as a directory.
            File dir = new File(file.getAbsolutePath());
            if (!dir.mkdirs()) {
                throw new ProfilingException(
                        "Cannot create profile directory at " + dir.getAbsolutePath());
            }

            // Create the directory and mark it writable.
            forceMkdir(dir);

            temporaryFiles.add(dir);
            return dir;
        } catch (IOException e) {
            throw new ProfilingException(
                    "Unable to create temporary file at " + baseDir.getAbsolutePath());
        }
    }

}
