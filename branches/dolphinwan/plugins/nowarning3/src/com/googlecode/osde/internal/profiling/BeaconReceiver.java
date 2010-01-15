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

package com.googlecode.osde.internal.profiling;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONValue;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * An HTTP server listening to a port, to which the Page Speed plugin will send
 * its beacon.
 *
 * @author Dolphin Chi-Ngai Wan
 */
public class BeaconReceiver {
    private Server server;
    private BeaconListener listener;

    public BeaconReceiver(int port, String path) {
        server = new Server(port);
        Context context = new Context(server, "/");
        context.addServlet(new ServletHolder(new FullBeaconServlet()), path);
    }

    /**
     * Starts listening for beacon.
     */
    public void start() throws Exception {
        server.start();
    }

    /**
     * Stops listening for beacon.
     */
    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Assigns a beacon listener which will receive notification when a beacon
     * is received.
     */
    public void setListener(BeaconListener listener) {
        this.listener = listener;
    }

    private class FullBeaconServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;
        private static final String TOKEN = "content=";

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            // The beacon is sent as a HTTP POST request body.
            String jsonAsString = IOUtils.toString(req.getInputStream(), "UTF-8");

            if (jsonAsString.startsWith(TOKEN)) {
                jsonAsString = jsonAsString.substring(TOKEN.length());
            }

            // The beacon is encoded with encodeURIComponent() by Page Speed
            // plugin.
            Map<String, Object> json = convert(jsonAsString);

            if (json != null && listener != null) {
                listener.beaconReceived(json);
            }
        }

        @SuppressWarnings({"unchecked"})
        private Map<String, Object> convert(String jsonAsString)
                throws UnsupportedEncodingException {
            return (Map<String, Object>) JSONValue.parse(URLDecoder.decode(jsonAsString, "UTF-8"));
        }
    }

}
