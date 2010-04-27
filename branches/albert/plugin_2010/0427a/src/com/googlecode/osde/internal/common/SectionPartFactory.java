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

package com.googlecode.osde.internal.common;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

/**
 * SectionPartFactory creates the SectionPart object and initializes with an IManagedForm instance.
 * @author Ching Yi, Chan
 */
public class SectionPartFactory {

    /**
     * @param section the section widget to use
     * @param form the managed form that manages the part
     * @return initialized SectionPart
     */
    public static SectionPart create(Section section, IManagedForm form) {
        SectionPart part = new SectionPart(section);
        part.initialize(form);
        return part;
    }
}
