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
package com.googlecode.osde.internal.gadgets.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Model object (Java Bean) that corresponds to the root "<messagebundle></messagebundle>"
 * element in the message bundle xml files. This model object contains only model
 * object Msg that in turn corresponds to the enclosed element "<msg>" in the "<messagebundle>"
 *
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 */
public class MessageBundle {

    protected List<Msg> messages;

    public MessageBundle() {
        messages = new ArrayList<Msg>();
    }

    public void addMessage(Msg msg) {
        messages.add(msg);
    }

    public void removeMessage(Msg msg) {
        messages.remove(msg);
    }

    public List<Msg> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder =
                new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        strBuilder.append("<messagebundle>\n");
        for (Iterator<Msg> iter = messages.iterator(); iter.hasNext();) {
            strBuilder.append("  "); // two spaces for indentation in the output
            strBuilder.append(iter.next().toString());
            strBuilder.append("\n");
        }
        strBuilder.append("</messagebundle>");

        return strBuilder.toString();
    }

    /**
     * Model object corresponds to "<msg></msg>" element in message bundle xml files
     */
    public static class Msg {

        protected String content; // enclosed content of <msg></msg>
        protected String name;       // attribute "name" of <msg> tag
        protected String desc;


        public Msg() {
        }

        public Msg(String name, String content, String desc) {
            this.name = name;
            this.content = content;
            this.desc = desc;
        }

        public Msg(String name, String content) {
            this.name = name;
            this.content = content;
            this.desc = "";
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public boolean equals(Object in) {
            if (this == in) {
                return true;
            }
            if (!(in instanceof Msg)) {
                return false;
            }

            Msg inMsg = (Msg) in;
            if (name == null || content == null || inMsg.name == null || inMsg.content == null) {
                return false;
            }
            return inMsg.name.equals(this.name) && inMsg.content.equals(this.content);
        }

        @Override
        public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("<msg");
            strBuilder.append(" name=\"");
            strBuilder.append(name);
            strBuilder.append("\"");
            // attribute desc is optional
            if (desc != null && desc.length() > 0) {
                strBuilder.append(" desc=\"");
                strBuilder.append(desc);
                strBuilder.append("\"");
            }
            strBuilder.append(">");
            strBuilder.append(content);
            strBuilder.append("</msg>");

            return strBuilder.toString();
        }
    }
}
