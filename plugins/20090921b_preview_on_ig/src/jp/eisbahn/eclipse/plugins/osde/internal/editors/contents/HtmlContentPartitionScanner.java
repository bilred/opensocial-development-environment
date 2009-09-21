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
package jp.eisbahn.eclipse.plugins.osde.internal.editors.contents;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class HtmlContentPartitionScanner extends RuleBasedPartitionScanner {

	public static final String TOKEN_TAG = "__tag__";
	public static final String TOKEN_HTML_COMMENT = "__html_comment__";
	public static final String TOKEN_SCRIPT = "__html_script__";
	
	public HtmlContentPartitionScanner() {
		super();
		Token tagToken = new Token(TOKEN_TAG);
		Token htmlCommentToken = new Token(TOKEN_HTML_COMMENT);
		Token scriptToken = new Token(TOKEN_SCRIPT);
		IPredicateRule[] rules = new IPredicateRule[4];
		rules[0] = new MultiLineRule("<!--", "-->", htmlCommentToken, (char)0, true);
		rules[1] = new MultiLineRule("<script", ">", scriptToken, (char)0, true);
		rules[2] = new SingleLineRule("</script", ">", scriptToken, (char)0, true);
		rules[3] = new MultiLineRule("<", ">", tagToken, (char)0, true);
		setPredicateRules(rules);
	}
	
}
