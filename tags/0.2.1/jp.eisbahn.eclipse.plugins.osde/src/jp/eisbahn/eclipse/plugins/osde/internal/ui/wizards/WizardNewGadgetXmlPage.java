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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createCheckbox;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createLabel;
import static jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.ComponentUtils.createText;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class WizardNewGadgetXmlPage extends WizardPage {

	private Text titleText;
	private Text titleUrlText;
	private Text descriptionText;
	private Text authorText;
	private Text authorEmailText;
	private Text screenshotText;
	private Text thumbnailText;
	private Button opensocial08Button;
	private Button opensocial07Button;
	private Button pubsubButton;
	private Button viewsButton;
	private Button flashButton;
	private Button skinsButton;
	private Button dynamicHeightButton;
	private Button setTitleButton;
	private Button miniMessageButton;
	private Button tabsButton;
	
	private Listener modifyListener = new Listener() {
		public void handleEvent(Event event) {
			boolean valid = validatePage();
			setPageComplete(valid);
		}
	};

	public WizardNewGadgetXmlPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createModulePrefsControls(composite);
		createFeaturesControls(composite);
		//
		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private void createModulePrefsControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group infoGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		infoGroup.setText("Application information");
		infoGroup.setFont(parent.getFont());
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		infoGroup.setLayout(layout);
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createLabel(infoGroup, "Title(*):");
		titleText = createText(infoGroup);
		titleText.addListener(SWT.Modify, modifyListener);
		createLabel(infoGroup, "Title URL:");
		titleUrlText = createText(infoGroup);
		createLabel(infoGroup, "Description:");
		descriptionText = new Text(infoGroup, SWT.MULTI | SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		layoutData.heightHint = 50;
		descriptionText.setLayoutData(layoutData);
		descriptionText.setFont(parent.getFont());
		createLabel(infoGroup, "Author:");
		authorText = createText(infoGroup);
		createLabel(infoGroup, "Author Email(*):");
		authorEmailText = createText(infoGroup);
		authorEmailText.addListener(SWT.Modify, modifyListener);
		createLabel(infoGroup, "Screen Shot:");
		screenshotText = createText(infoGroup);
		createLabel(infoGroup, "Thumbnail:");
		thumbnailText = createText(infoGroup);
		Label noticeLabel = createLabel(infoGroup, "The fields which has (*) mark are required.");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalAlignment = GridData.END;
		layoutData.horizontalSpan = 4;
		noticeLabel.setLayoutData(layoutData);
	}
	
	private void createFeaturesControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group featuresGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		featuresGroup.setText("Features");
		featuresGroup.setFont(parent.getFont());
		featuresGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		featuresGroup.setLayout(layout);
		featuresGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		opensocial08Button = createCheckbox(featuresGroup, "OpenSocial v0.8");
		opensocial08Button.setSelection(true);
		opensocial07Button = createCheckbox(featuresGroup, "OpenSocial v0.7");
		pubsubButton = createCheckbox(featuresGroup, "PubSub");
		viewsButton = createCheckbox(featuresGroup, "Views");
		flashButton = createCheckbox(featuresGroup, "Flash");
		skinsButton = createCheckbox(featuresGroup, "Skins");
		dynamicHeightButton = createCheckbox(featuresGroup, "Dynamic Height");
		setTitleButton = createCheckbox(featuresGroup, "Set Title");
		miniMessageButton = createCheckbox(featuresGroup, "Mini Message");
		tabsButton = createCheckbox(featuresGroup, "Tabs");
	}
	
	private boolean validatePage() {
		String title = titleText.getText().trim();
		if (title.length() == 0) {
			setErrorMessage(null);
			setMessage("Title is empty.");
			return false;
		}
		String authorEmail = authorEmailText.getText().trim();
		if (authorEmail.length() == 0) {
			setErrorMessage(null);
			setMessage("Author Email is empty.");
			return false;
		}
		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	public GadgetXmlData getInputedData() {
		GadgetXmlData data = new GadgetXmlData();
		data.setAuthor(authorText.getText().trim());
		data.setAuthorEmail(authorEmailText.getText().trim());
		data.setDescription(descriptionText.getText().trim());
		data.setDynamicHeight(dynamicHeightButton.getSelection());
		data.setFlash(flashButton.getSelection());
		data.setMiniMessage(miniMessageButton.getSelection());
		data.setOpensocial07(opensocial07Button.getSelection());
		data.setOpensocial08(opensocial08Button.getSelection());
		data.setPubsub(pubsubButton.getSelection());
		data.setScreenshot(screenshotText.getText().trim());
		data.setSetTitle(setTitleButton.getSelection());
		data.setSkins(skinsButton.getSelection());
		data.setTabs(tabsButton.getSelection());
		data.setThumbnail(thumbnailText.getText().trim());
		data.setTitle(titleText.getText().trim());
		data.setTitleUrl(titleUrlText.getText().trim());
		data.setViews(viewsButton.getSelection());
		return data;
	}

}
