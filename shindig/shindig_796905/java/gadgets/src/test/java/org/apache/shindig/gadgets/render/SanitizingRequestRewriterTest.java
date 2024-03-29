/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.gadgets.render;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.io.IOUtils;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.HttpResponseBuilder;
import org.apache.shindig.gadgets.parse.caja.CajaCssParser;
import org.apache.shindig.gadgets.parse.caja.CajaCssSanitizer;
import org.apache.shindig.gadgets.rewrite.BaseRewriterTestCase;
import org.apache.shindig.gadgets.rewrite.ContentRewriterFeatureFactory;
import org.apache.shindig.gadgets.rewrite.MutableContent;
import org.apache.shindig.gadgets.rewrite.RequestRewriter;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class SanitizingRequestRewriterTest extends BaseRewriterTestCase {
  private static final Uri CONTENT_URI = Uri.parse("www.example.org/content");

  private String rewrite(HttpRequest request, HttpResponse response) throws Exception {
    request.setSanitizationRequested(true);
    RequestRewriter rewriter = createRewriter(Collections.<String>emptySet(),
        Collections.<String>emptySet());

    MutableContent mc = new MutableContent(parser, response);
    if (!rewriter.rewrite(request, response, mc)) {
      return null;
    }
    return mc.getContent();
  }

  private RequestRewriter createRewriter(Set<String> tags, Set<String> attributes) {
    ContentRewriterFeatureFactory rewriterFeatureFactory =
        new ContentRewriterFeatureFactory(null, ".*", "", "HTTP", "embed,img,script,link,style");
    return new SanitizingRequestRewriter(rewriterFeatureFactory,
        rewriterUris, new CajaCssSanitizer(new CajaCssParser()));
  }

  @Test
  public void enforceInvalidProxedCssRejected() throws Exception {
    HttpRequest req = new HttpRequest(CONTENT_URI);
    req.setRewriteMimeType("text/css");
    HttpResponse response = new HttpResponseBuilder().setResponseString("doEvil()").create();
    String sanitized = "";
    assertEquals(sanitized, rewrite(req, response));
  }

  @Test
  public void enforceValidProxedCssAccepted() throws Exception {
    HttpRequest req = new HttpRequest(CONTENT_URI);
    req.setRewriteMimeType("text/css");
    HttpResponse response = new HttpResponseBuilder().setResponseString(
        "@import url('http://www.evil.com/more.css'); A { font : BOLD }").create();
    // The caja css sanitizer does *not* remove the initial colon in urls
    // since this does not work in IE
    String sanitized = 
      "@import url('http://www.test.com/dir/proxy?"
        + "url=http%3A%2F%2Fwww.evil.com%2Fmore.css"
        + "&fp=45508&sanitize=1&rewriteMime=text%2Fcss');\n"
        + "A {\n"
        + "  font: BOLD\n"
        + "}";
    String rewritten = rewrite(req, response);
    assertEquals(sanitized, rewritten);
  }

  @Test
  public void enforceInvalidProxedImageRejected() throws Exception {
    HttpRequest req = new HttpRequest(CONTENT_URI);
    req.setRewriteMimeType("image/*");
    HttpResponse response = new HttpResponseBuilder().setResponse("NOTIMAGE".getBytes()).create();
    String sanitized = "";
    assertEquals(sanitized, rewrite(req, response));
  }

  @Test
  public void validProxiedImageAccepted() throws Exception {
    HttpRequest req = new HttpRequest(CONTENT_URI);
    req.setRewriteMimeType("image/*");
    HttpResponse response = new HttpResponseBuilder().setResponse(
        IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(
            "org/apache/shindig/gadgets/rewrite/image/inefficient.png"))).create();
    assertNull(rewrite(req, response));
  }
}
