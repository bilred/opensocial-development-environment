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
 * specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.osde.internal.igoogle;

import com.googlecode.osde.internal.utils.Logger;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;

/**
 * The Action for processing publish a gadget against iGoogle server.
 *
 * @author albert.cheng.ig@gmail.com
 */
public class IgPublishAction extends IgBaseAction {
    private static Logger logger = new Logger(IgPublishAction.class);

    public void run(IAction action) {
        logger.fine("in run");
        IgPublishDialog dialog = new IgPublishDialog(getShell());
        int openResult = dialog.open();
        if (openResult == Window.OK) {
            String hostProjectName = dialog.getGadgetUrl();
            Job job = new IgPublishJob(getShell(), hostProjectName);
            job.schedule();
        }
    }
}
