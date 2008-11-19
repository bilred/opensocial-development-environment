package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class HtmlContentPartitionScanner extends RuleBasedPartitionScanner {

	public static final String TOKEN_TAG = "__tag__";
	public static final String TOKEN_HTML_COMMENT = "__html_comment__";
	
	public HtmlContentPartitionScanner() {
		super();
		Token tagToken = new Token(TOKEN_TAG);
		Token htmlCommentToken = new Token(TOKEN_HTML_COMMENT);
		IPredicateRule[] rules = new IPredicateRule[2];
		rules[0] = new MultiLineRule("<!--", "-->", htmlCommentToken, (char)0, true);
		rules[1] = new MultiLineRule("<", ">", tagToken, (char)0, true);
		setPredicateRules(rules);
	}
	
}
