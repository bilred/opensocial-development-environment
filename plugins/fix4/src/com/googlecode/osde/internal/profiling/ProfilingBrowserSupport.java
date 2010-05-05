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

import java.net.URL;

import com.googlecode.osde.internal.Activator;
import com.googlecode.osde.internal.OsdeConfig;
import com.googlecode.osde.internal.common.AbstractJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.AbstractWebBrowser;
import org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * A browser support that searches for any Firefox installation, creates an
 * user profile, then opens an URL with the installation and the profile.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class ProfilingBrowserSupport extends AbstractWorkbenchBrowserSupport {
    private final String profileName = "osde";
    private final Shell shell;

    public ProfilingBrowserSupport(Shell shell) {
        this.shell = shell;
    }

    /**
     * Creates an external Firefox browser.
     */
    public IWebBrowser createBrowser(int style, String browserId, String name, String tooltip)
            throws PartInitException {
        return createBrowser(browserId);
    }

    /**
     * Creates an external Firefox browser.
     */
    public IWebBrowser createBrowser(String browserId) throws PartInitException {
        return new FirefoxBrowser(browserId);
    }

    private class FirefoxBrowser extends AbstractWebBrowser {
        FirefoxBrowser(String id) {
            super(id);
        }

        public void openURL(URL url) throws PartInitException {
            // Opening an external application takes time. So we do it in
            // another thread without blocking the UI thread.
            new OpenWebBrowserJob(url).schedule();
        }
    }

    private class OpenWebBrowserJob extends AbstractJob {
        private final URL url;

        public OpenWebBrowserJob(URL url) {
            super("Opening Firefox Browser");
            this.url = url;
        }

        @Override
        protected void runImpl(IProgressMonitor monitor) throws Exception {
            monitor.beginTask("Opening Firefox Browser", 3);

            OsdeConfig config = Activator.getDefault().getOsdeConfiguration();
            String location = config.getFirefoxLocation();

            monitor.subTask("Create a Firefox user profile dedicated for performance profiling");
            FirefoxBinary binary = new FirefoxBinary(location);
            Profile profile = binary.createProfile(profileName);
            monitor.worked(1);

            monitor.subTask("Install Firebug and PageSpeed plugins");
            profile.installPageSpeed();
            monitor.worked(1);

            monitor.subTask("Launch Firefox browser window");
            binary.launch(profile, url.toExternalForm());
            monitor.worked(1);
        }
    }
}
