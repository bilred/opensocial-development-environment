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
package org.apache.shindig.social.opensocial.hibernate.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.JsonSerializer;
import org.json.JSONObject;

public class HttpLogFilter implements Filter {

	private static Log logger = LogFactory.getLog(HttpLogFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		req = new BufferedServletRequestWrapper(req);
		try {
			JSONObject json = new JSONObject();
			json.put("requestURI", req.getRequestURI());
			json.put("contentType", req.getContentType());
			json.put("method", req.getMethod());
			Map<String, String> headerMap = new HashMap<String, String>();
			Enumeration headerNames = req.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = (String) headerNames.nextElement();
				String headerValue = req.getHeader(headerName);
				headerMap.put(headerName, headerValue);
			}
			json.put("headerMap", headerMap);
			InputStream in = req.getInputStream();
			String body = IOUtils.toString(in, "UTF-8");
			json.put("body", body);
			
			json.put("parameterMap", req.getParameterMap());
			json.put("timestamp", new Date().getTime());
			//
			String serialized = JsonSerializer.serialize(json);
			logger.info(serialized);
		} catch (Throwable e) {
			logger.error("Couldn't output a log.", e);
		}
		chain.doFilter(req, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public class BufferedServletInputStream extends ServletInputStream {

		private ByteArrayInputStream inputStream;

		public BufferedServletInputStream(byte[] buffer) {
			this.inputStream = new ByteArrayInputStream(buffer);
		}

		@Override
		public int available() throws IOException {
			return inputStream.available();
		}

		@Override
		public int read() throws IOException {
			return inputStream.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return inputStream.read(b, off, len);
		}
	}

	public class BufferedServletRequestWrapper extends
			HttpServletRequestWrapper {

		private byte[] buffer;
		private Map parameterMap;

		public BufferedServletRequestWrapper(HttpServletRequest request)
				throws IOException {
			super(request);

			parameterMap = new HashMap();
			parameterMap.putAll(request.getParameterMap());

			InputStream is = request.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int read;
			while ((read = is.read(buff)) > 0) {
				baos.write(buff, 0, read);
			}

			this.buffer = baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return new BufferedServletInputStream(this.buffer);
		}

		@Override
		public String getParameter(String name) {
			Object value = parameterMap.get(name);
			if (value != null) {
				if (value instanceof String[]) {
					String[] values = (String[])value;
					if (values.length > 0) {
						return values[0];
					} else {
						return null;
					}
				} else if (value instanceof String) {
					return (String)value;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		@Override
		public Map getParameterMap() {
			return parameterMap;
		}

		@Override
		public Enumeration getParameterNames() {
			return IteratorUtils.asEnumeration(parameterMap.keySet().iterator());
		}

		@Override
		public String[] getParameterValues(String name) {
			Object value = parameterMap.get(name);
			if (value != null) {
				if (value instanceof String[]) {
					return (String[])value;
				} else if (value instanceof String) {
					return new String[]{(String)value};
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

}
