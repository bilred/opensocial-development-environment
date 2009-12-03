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
package jp.eisbahn.eclipse.plugins.osde.internal.runtime;

import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs.UserPrefsView;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * A utility class for notifying User Preference View.
 */
public class UserPrefsViewNotifier {
    private static final Logger logger = new Logger(UserPrefsViewNotifier.class);
	public static void fire(final LaunchApplicationInformation information, Shell shell, IWorkbenchPart targetPart) {
		IProject project = information.getProject();
		final String url = "http://localhost:8080/" + project.getName().replace(" ", "%20") + "/" + information.getUrl().replace(" ", "%20");
		final IWorkbenchWindow window = targetPart.getSite().getWorkbenchWindow();
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					UserPrefsView userPrefsView;
					userPrefsView = (UserPrefsView)window.getActivePage().showView(UserPrefsView.ID);
					userPrefsView.showUserPrefFields(information, url);
				} catch (PartInitException e) {
					logger.error("Notifying to UserPrefs view failed.", e);
					throw new IllegalStateException(e);
				}
			}
		});
	}

}
