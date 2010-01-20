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
package com.googlecode.osde.internal.ui.views.apps;

import java.util.List;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

class ApplicationsBlock extends MasterDetailsBlock {

    private ApplicationsPart applicationsPart;
    private ApplicationView applicationView;

    public ApplicationsBlock(ApplicationView applicationView) {
        super();
        this.applicationView = applicationView;
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
        applicationsPart = new ApplicationsPart(parent, managedForm, applicationView);
        managedForm.addPart(applicationsPart);
    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
        final IDetailsPage detailsPage = new ApplicationPage(applicationView);
        detailsPart.registerPage(Person.class, detailsPage);
        detailsPart.setPageProvider(new IDetailsPageProvider() {

            public IDetailsPage getPage(Object key) {
                if (key.equals(ApplicationImpl.class)) {
                    return detailsPage;
                }
                return null;
            }

            public Object getPageKey(Object object) {
                if (object instanceof ApplicationImpl) {
                    return ApplicationImpl.class;
                }
                return object.getClass();
            }

        });
        sashForm.setWeights(new int[] {35, 65});
    }

    public void setApplications(List<ApplicationImpl> applications) {
        applicationsPart.setApplications(applications);
    }
}