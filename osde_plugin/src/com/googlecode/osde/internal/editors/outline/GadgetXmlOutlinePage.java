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
package com.googlecode.osde.internal.editors.outline;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.googlecode.osde.internal.editors.GadgetXmlEditor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class GadgetXmlOutlinePage extends ContentOutlinePage {
	
	private GadgetXmlEditor editor;

	public GadgetXmlOutlinePage(GadgetXmlEditor editor) {
		super();
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer treeViewer = getTreeViewer();
		treeViewer.setContentProvider(new GadgetXmlOutlineContentProvider());
		treeViewer.setLabelProvider(new GadgetXmlOutlineLabelProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection structured = (IStructuredSelection)event.getSelection();
				ElementModel model = (ElementModel)structured.getFirstElement();
				if (model != null) {
					int lineNumber = model.getLineNumber();
					editor.activateSourceEditor(lineNumber);
				}
			}
		});
		//
		try {
			String source = editor.getSource();
			ElementModel module = new GadgetXmlParser().parse(source);
			treeViewer.setInput(module);
			treeViewer.expandAll();
		} catch(SAXParseException e) {
			treeViewer.setInput(null);
		} catch(SAXException e) {
			treeViewer.setInput(null);
		} catch (ParserConfigurationException e) {
			treeViewer.setInput(null);
		} catch (IOException e) {
			treeViewer.setInput(null);
		}
	}
	
	public void refresh() {
		TreeViewer treeViewer = getTreeViewer();
		try {
			String source = editor.getSource();
			ElementModel module = new GadgetXmlParser().parse(source);
			treeViewer.setInput(module);
			treeViewer.expandAll();
		} catch(SAXParseException e) {
			treeViewer.setInput(null);
		} catch(SAXException e) {
			treeViewer.setInput(null);
		} catch (ParserConfigurationException e) {
			treeViewer.setInput(null);
		} catch (IOException e) {
			treeViewer.setInput(null);
		}
	}
	
}