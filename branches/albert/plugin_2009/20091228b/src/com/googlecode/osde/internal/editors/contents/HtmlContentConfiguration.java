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

import com.googlecode.osde.internal.Activator;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

public class HtmlContentConfiguration extends SourceViewerConfiguration {

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				HtmlContentPartitionScanner.TOKEN_HTML_COMMENT,
				HtmlContentPartitionScanner.TOKEN_TAG,
				HtmlContentPartitionScanner.TOKEN_SCRIPT
		};
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		Activator activator = Activator.getDefault();
		// script part
		DefaultDamagerRepairer scriptDamageRepairer = new DefaultDamagerRepairer(
				new SingleTokenScanner(new TextAttribute(activator.getColor(new RGB(0, 128, 150)))));
		reconciler.setDamager(scriptDamageRepairer, HtmlContentPartitionScanner.TOKEN_SCRIPT);
		reconciler.setRepairer(scriptDamageRepairer, HtmlContentPartitionScanner.TOKEN_SCRIPT);
		// html comment part
		DefaultDamagerRepairer htmlCommentDamageRepairer = new DefaultDamagerRepairer(
				new SingleTokenScanner(new TextAttribute(activator.getColor(new RGB(100, 100, 100)))));
		reconciler.setDamager(htmlCommentDamageRepairer, HtmlContentPartitionScanner.TOKEN_HTML_COMMENT);
		reconciler.setRepairer(htmlCommentDamageRepairer, HtmlContentPartitionScanner.TOKEN_HTML_COMMENT);
		// tag part
		DefaultDamagerRepairer tagDamageRepairer = new DefaultDamagerRepairer(
				new SingleTokenScanner(new TextAttribute(activator.getColor(new RGB(0, 128, 0)))));
		reconciler.setDamager(tagDamageRepairer, HtmlContentPartitionScanner.TOKEN_TAG);
		reconciler.setRepairer(tagDamageRepairer, HtmlContentPartitionScanner.TOKEN_TAG);
		// default
		DefaultDamagerRepairer defaultDamageRepairer = new DefaultDamagerRepairer(
				new SingleTokenScanner(new TextAttribute(activator.getColor(new RGB(0, 0, 0)))));
		reconciler.setDamager(defaultDamageRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(defaultDamageRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		//
		return reconciler;
	}

}
