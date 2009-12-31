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

import com.google.gadgets.model.Module;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class ContentsPage extends FormPage {

    private Module module;

    private ContentsBlock block;

    public Module getModule() {
        return module;
    }

    public ContentsPage(FormEditor editor, Module module) {
        super(editor, null, "Contents");
        this.module = module;
        block = new ContentsBlock(this);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        block.createContent(managedForm);
    }

    public void updateContentsModel() {
        block.updateContentModel();
    }

    public List<ContentModel> getContentModels() {
        return block.getContentModels();
    }

    public void updateModel() {
        block.updateModel();
    }

    public void changeModel(Module model) {
        this.module = model;
        block.changeModel();
    }

}
