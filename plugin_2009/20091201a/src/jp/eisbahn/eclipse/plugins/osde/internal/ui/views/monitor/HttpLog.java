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
package jp.eisbahn.eclipse.plugins.osde.internal.ui.views.monitor;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.json.simple.JSONObject;

public class HttpLog implements Comparable<HttpLog> {
	
	private JSONObject json;
	private long timestamp;
	
	public HttpLog(JSONObject json) {
		super();
		this.json = json;
		timestamp = (Long)json.get("timestamp");
	}
	
	public JSONObject getJSON() {
		return json;
	}

	public int compareTo(HttpLog o) {
		return new CompareToBuilder().append(o.timestamp, timestamp).toComparison();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof HttpLog) {
			return timestamp == ((HttpLog)o).timestamp;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int)timestamp;
	}

}
