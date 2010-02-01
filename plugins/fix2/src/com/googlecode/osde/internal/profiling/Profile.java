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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;


/**
 * A Firefox user profile. This is a simplified version of
 * <code>org.openqa.selenium.firefox.FirefoxProfile</code>.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class Profile {
    private final File extensionsDir;
    private final File userPreferenceFile;
    private final File profileDir;
    final String name;

    /**
     * Constructs a firefox profile from an existing, physical profile folder.
     */
    Profile(File profileDir, String name) {
        this.name = name;
        this.profileDir = profileDir;
        this.extensionsDir = new File(profileDir, "extensions");
        this.userPreferenceFile = new File(profileDir, "user.js");

        if (!profileDir.exists()) {
            throw new ProfilingException(
                    MessageFormat.format("Profile directory does not exist: {0}",
                            profileDir.getAbsolutePath()));
        }
    }

    /**
     * Returns true if there is an Firefox instance running with this profile.
     */
    boolean isRunning() {
        File macAndLinuxLockFile = new File(profileDir, ".parentlock");
        File windowsLockFile = new File(profileDir, "parent.lock");

        return macAndLinuxLockFile.exists() || windowsLockFile.exists();
    }

    void installPageSpeed() throws IOException {
        // TODO: collect beacons in port 8900 in the future.
        installPageSpeed(null);
    }

    void installPageSpeed(String beaconUrl) throws IOException {
        addExtension("firebug-1.4.5-fx.xpi", "firebug@software.joehewitt.com");
        addExtension("pagespeed-1.5b.xpi", "{e3f6c2cc-d8db-498c-af6c-499fb211db97}");
        deleteExtensionsCacheIfItExists();
        saveUserPrefs(beaconUrl);
    }

    /**
     * Installs a Firefox extension into this user profile.
     *
     * @param classpath The classpath of extension XPI file.
     * @param id The extension id.
     */
    private void addExtension(String classpath, String id) throws IOException {
        // Firefox looks up extensions in 
        // <profiledir>/<extensions>/<extension-id>.
        File extensionDirectory = new File(extensionsDir, id);
        if (extensionDirectory.exists() && extensionDirectory.isDirectory()) {
            // we assume the extension is already installed and will not do it
            // again.
            return;
        }

        InputStream resource = Profile.class.getResourceAsStream(classpath);
        if (resource == null && !classpath.startsWith("/")) {
            resource = Profile.class.getResourceAsStream("/" + classpath);
        }

        if (resource == null) {
            throw new FileNotFoundException("Cannot locate resource with name: " + classpath);
        }

        forceMkdir(extensionDirectory);
        unzip(resource, extensionDirectory);
    }

    private Map<String, String> readExistingPrefs(File userPrefs) {
        Map<String, String> prefs = new HashMap<String, String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(userPrefs));
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("user_pref(\"")) {
                    line = reader.readLine();
                    continue;
                }
                line = line.substring("user_pref(\"".length());
                line = line.substring(0, line.length() - ");".length());
                String[] parts = line.split(",");
                parts[0] = parts[0].substring(0, parts[0].length() - 1);
                prefs.put(parts[0].trim(), parts[1].trim());

                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ProfilingException(e);
        } finally {
            closeQuietly(reader);
        }

        return prefs;
    }

    private void saveUserPrefs(String beaconUrl) {
        Map<String, String> prefs = new HashMap<String, String>();

        if (userPreferenceFile.exists()) {
            prefs = readExistingPrefs(userPreferenceFile);
            if (!userPreferenceFile.delete()) {
                throw new ProfilingException("Cannot delete existing user preferences");
            }
        }

        // Normal settings to facilitate testing
        prefs.put("app.update.auto", "false");
        prefs.put("app.update.enabled", "false");
        prefs.put("browser.download.manager.showWhenStarting", "false");
        prefs.put("browser.EULA.override", "true");
        prefs.put("browser.EULA.3.accepted", "true");
        prefs.put("browser.link.open_external", "2");
        prefs.put("browser.link.open_newwindow", "2");
        prefs.put("browser.safebrowsing.enabled", "false");
        prefs.put("browser.search.update", "false");
        prefs.put("browser.search.suggest.enabled", "false");
        prefs.put("browser.sessionstore.resume_from_crash", "false");
        prefs.put("browser.shell.checkDefaultBrowser", "false");
        prefs.put("browser.startup.page", "0");
        prefs.put("browser.tabs.warnOnClose", "false");
        prefs.put("browser.tabs.warnOnOpen", "false");
        prefs.put("dom.disable_open_during_load", "false");
        prefs.put("extensions.update.enabled", "false");
        prefs.put("extensions.update.notifyUser", "false");
        prefs.put("security.fileuri.origin_policy", "3");
        prefs.put("security.fileuri.strict_origin_policy", "false");
        prefs.put("security.warn_entering_secure", "false");
        prefs.put("security.warn_submit_insecure", "false");
        prefs.put("security.warn_entering_secure.show_once", "false");
        prefs.put("security.warn_entering_weak", "false");
        prefs.put("security.warn_entering_weak.show_once", "false");
        prefs.put("security.warn_leaving_secure", "false");
        prefs.put("security.warn_leaving_secure.show_once", "false");
        prefs.put("security.warn_submit_insecure", "false");
        prefs.put("security.warn_viewing_mixed", "false");
        prefs.put("security.warn_viewing_mixed.show_once", "false");
        prefs.put("signon.rememberSignons", "false");
        prefs.put("startup.homepage_welcome_url", "\"about:blank\"");

        prefs.put("extensions.firebug.allPagesActivation", "\"on\"");
        prefs.put("extensions.firebug.defaultPanelName", "\"pagespeed\"");
        prefs.put("extensions.PageSpeed.autorun", "true");

        if (beaconUrl != null) {
            prefs.put("extensions.PageSpeed.beacon.full_results.url", "\"" + beaconUrl + "\"");
            prefs.put("extensions.PageSpeed.beacon.full_results.enabled", "true");
            prefs.put("extensions.PageSpeed.beacon.full_results.autorun", "true");
            prefs.put("extensions.PageSpeed.quit_after_scoring", "true");
        }

        writeNewPrefs(prefs);
    }

    private void deleteExtensionsCacheIfItExists() {
        File cacheFile = new File(extensionsDir, "../extensions.cache");
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    private void writeNewPrefs(Map<String, String> prefs) {
        Writer writer = null;
        try {
            writer = new FileWriter(userPreferenceFile);
            for (Map.Entry<String, String> entry : prefs.entrySet()) {
                writer.append(
                        String.format("user_pref(\"%s\", %s);\n", entry.getKey(), entry.getValue())
                );
            }
        } catch (IOException e) {
            throw new ProfilingException(e);
        } finally {
            closeQuietly(writer);
        }
    }

    /**
     * Unzips a zip content into a physical folder.
     *
     * @return The newly-created folder with the unzipped content.
     */
    private static File unzip(InputStream resource, File output) throws IOException {
        // Allocate a 16K buffer to read the XPI file faster.
        ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(resource, 16 * 1024));
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

    private static void unzipFile(File target, InputStream zipStream)
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
}
