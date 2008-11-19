package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

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
				HtmlContentPartitionScanner.TOKEN_TAG
		};
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		Activator activator = Activator.getDefault();
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
