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
package com.googlecode.osde.internal.editors.contents;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

public class ContentsBlock extends MasterDetailsBlock {

    private SupportedViewsPart supportedViewsPart;

    private ContentsPage page;

    public ContentsBlock(ContentsPage page) {
        super();
        this.page = page;
    }

    @Override
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
        supportedViewsPart = new SupportedViewsPart(parent, managedForm, page);
        managedForm.addPart(supportedViewsPart);
    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
        final IDetailsPage detailsPage = new ContentPage(page);
        detailsPart.registerPage(ContentModel.class, detailsPage);
        detailsPart.setPageProvider(new IDetailsPageProvider() {

            public IDetailsPage getPage(Object key) {
                if (key.equals(ContentModel.class)) {
                    return detailsPage;
                }
                return null;
            }

            public Object getPageKey(Object object) {
                if (object instanceof ContentModel) {
                    return ContentModel.class;
                }
                return object.getClass();
            }

        });
        sashForm.setWeights(new int[] {45, 55});
    }

    public void updateContentModel() {
        supportedViewsPart.markDirty();
    }

    public List<ContentModel> getContentModels() {
        return supportedViewsPart.getContentModels();
    }

    public void updateModel() {
        if (supportedViewsPart != null) {
            supportedViewsPart.setValuesToModule();
        }
    }

    public void changeModel() {
        if (supportedViewsPart != null) {
            supportedViewsPart.changeModel();
        }
    }

}