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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.IOUtils.closeQuietly;


/**
 * A Firefox user profile.
 * <p>
 * Note that it is a much simplified version of WebDriver's FirefoxProfile and
 * Preferences.
 *
 * @author Dolphin Chi-Ngai Wan
 */
class Profile {
    private static final String EM_NAMESPACE_URI = "http://www.mozilla.org/2004/em-rdf#";

    private File extensionsDir;
    private File userPreferenceFile;
    private final ZipHandler zipHandler;

    /**
     * Constructs a firefox profile from an existing, physical profile folder.
     */
    Profile(File profileDir) {
        this.extensionsDir = new File(profileDir, "extensions");
        this.userPreferenceFile = new File(profileDir, "user.js");
        this.zipHandler = new ZipHandler();

        if (!profileDir.exists()) {
            throw new ProfilingException(MessageFormat.format("Profile directory does not exist: {0}",
                    profileDir.getAbsolutePath()));
        }
    }

    public void installPageSpeed(String beaconUrl) throws IOException {
        addExtension("firebug-1.4.5-fx.xpi");
        addExtension("pagespeed-1.5b.xpi");
        deleteExtensionsCacheIfItExists();
        saveUserPrefs(beaconUrl);
    }

    private File addExtension(String classpath) throws IOException {
        InputStream resource = Profile.class.getResourceAsStream(classpath);
        if (resource == null && !classpath.startsWith("/")) {
            resource = Profile.class.getResourceAsStream("/" + classpath);
        }

        if (resource == null) {
            throw new FileNotFoundException("Cannot locate resource with name: " + classpath);
        }

        File root;
        try {
            if (zipHandler.isZipped(classpath)) {
                root = zipHandler.unzip(resource);
            } else {
                throw new ProfilingException("Only install zipped extensions");
            }

            addExtension(root);
        } finally {
            zipHandler.cleanTemporaryFiles();
        }
        return root;
    }

    /**
     * Attempts to add an extension to install into this instance.
     *
     * @param extensionToInstall An Firefox extension folder.
     */
    private void addExtension(File extensionToInstall) throws IOException {
        if (!extensionToInstall.isDirectory() &&
                !zipHandler.isZipped(extensionToInstall.getAbsolutePath())) {
            throw new IOException("Can only install from a zip file, an XPI or a directory");
        }

        File root = obtainRootDirectory(extensionToInstall);

        String id = readIdFromInstallRdf(root);

        File extensionDirectory = new File(extensionsDir, id);

        if (extensionDirectory.exists()) {
            forceDelete(extensionDirectory);
        }

        forceMkdir(extensionDirectory);
        copyDirectory(root, extensionDirectory);
    }

    private String readIdFromInstallRdf(File root) {
        try {
            File installRdf = new File(root, "install.rdf");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(installRdf);

            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if ("em".equals(prefix)) {
                        return EM_NAMESPACE_URI;
                    } else if ("RDF".equals(prefix)) {
                        return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                    }

                    return XMLConstants.NULL_NS_URI;
                }

                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException("getPrefix");
                }

                public Iterator<?> getPrefixes(String uri) {
                    throw new UnsupportedOperationException("getPrefixes");
                }
            });

            Node idNode = (Node) xpath.compile("//em:id").evaluate(doc, XPathConstants.NODE);

            String id;
            if (idNode == null) {
                Node descriptionNode =
                        (Node) xpath.compile("//RDF:Description")
                                .evaluate(doc, XPathConstants.NODE);
                Node idAttr =
                        descriptionNode.getAttributes().getNamedItemNS(EM_NAMESPACE_URI, "id");
                if (idAttr == null) {
                    throw new ProfilingException(
                            "Cannot locate node containing extension id: " +
                                    installRdf.getAbsolutePath());
                }
                id = idAttr.getNodeValue();
            } else {
                id = idNode.getTextContent();
            }

            if (id == null || "".equals(id.trim())) {
                throw new FileNotFoundException("Cannot install extension with ID: " + id);
            }
            return id;
        } catch (Exception e) {
            throw new ProfilingException(e);
        }
    }

    private File obtainRootDirectory(File extensionToInstall) throws IOException {
        File root = extensionToInstall;
        if (!extensionToInstall.isDirectory()) {
            BufferedInputStream bis =
                    new BufferedInputStream(new FileInputStream(extensionToInstall));
            try {
                root = zipHandler.unzip(bis);
            } finally {
                bis.close();
            }
        }
        return root;
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
        prefs.put("extensions.PageSpeed.beacon.full_results.url", "\"" + beaconUrl + "\"");
        prefs.put("extensions.PageSpeed.beacon.full_results.enabled", "true");
        prefs.put("extensions.PageSpeed.beacon.full_results.autorun", "true");
        prefs.put("extensions.PageSpeed.autorun", "true");
        prefs.put("extensions.PageSpeed.quit_after_scoring", "true");

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
}
