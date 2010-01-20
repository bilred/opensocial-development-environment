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
package com.googlecode.osde.internal.ui.views.filters;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.googlecode.osde.internal.OsdeProjectNature;
import com.googlecode.osde.internal.utils.Logger;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class NonOsdeProjectFilter extends ViewerFilter {

    private static Logger logger = new Logger(NonOsdeProjectFilter.class);

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) element;
            Object adapted = adaptable.getAdapter(IProject.class);
            if (adapted instanceof IProject) {
                IProject project = (IProject) adapted;
                try {
                    return project.isOpen() && project.hasNature(OsdeProjectNature.ID);
                } catch (CoreException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;

    }

}
