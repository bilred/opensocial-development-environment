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
package com.googlecode.osde.internal.ui;

import com.googlecode.osde.internal.ui.views.activities.ActivitiesView;
import com.googlecode.osde.internal.ui.views.appdata.AppDataView;
import com.googlecode.osde.internal.ui.views.docs.DocumentView;
import com.googlecode.osde.internal.ui.views.people.PersonView;
import com.googlecode.osde.internal.ui.views.userprefs.UserPrefsView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * Eclipse Perspective for OSDE.
 */
public class OsdePerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout factory) {
		String editorArea = factory.getEditorArea();
		//
		IFolderLayout left = factory.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
		left.addView(IPageLayout.ID_RES_NAV);
		//
		IFolderLayout bottom = factory.createFolder("bottom", IPageLayout.BOTTOM, 0.70f, editorArea);
		bottom.addView(PersonView.ID);
		bottom.addView(ActivitiesView.ID);
		bottom.addView(AppDataView.ID);
		bottom.addView(DocumentView.ID);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		// Such that plugin user can access the compiler error/warnings easily.
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		//
		IFolderLayout leftBottom = factory.createFolder("leftBottom", IPageLayout.BOTTOM, 0.6f, "left");
		leftBottom.addView(IPageLayout.ID_OUTLINE);
		leftBottom.addView(UserPrefsView.ID);
		//
		factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		//
		factory.addActionSet("org.eclipse.team.ui.actionSet");
		factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet");
		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		//
		factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective");
		factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		//
		factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		//
		factory.addShowViewShortcut(PersonView.ID);
		factory.addShowViewShortcut(ActivitiesView.ID);
		factory.addShowViewShortcut(AppDataView.ID);
		factory.addShowViewShortcut(UserPrefsView.ID);
		factory.addShowViewShortcut(DocumentView.ID);
		factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView");
		factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView");
		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}

}
