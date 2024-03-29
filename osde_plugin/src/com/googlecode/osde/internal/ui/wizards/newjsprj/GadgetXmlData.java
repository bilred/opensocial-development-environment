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
package com.googlecode.osde.internal.ui.wizards.newjsprj;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GadgetXmlData implements Serializable {

    private String title = "";
    private String titleUrl = "";
    private String description = "";
    private String author = "";
    private String authorEmail = "";
    private String screenshot = "";
    private String thumbnail = "";

    // OSDE supports 4 Feature's: opensocial, opensocial v0.9, opensocial v0.8, opensocial v0.7
    // "nonOpensocial" means no opensocial Feature is supported.
    private boolean opensocial;
    private boolean opensocial09;
    private boolean opensocial08;
    private boolean opensocial07;
    private boolean nonOpensocial;

    private boolean pubsub;
    private boolean views;
    private boolean flash;
    private boolean dynamicHeight;
    private boolean setTitle;
    private boolean skins;
    private boolean miniMessage;
    private boolean tabs;
    private String gadgetSpecFilename = "";

    public String getGadgetSpecFilename() {
        return gadgetSpecFilename;
    }

    public void setGadgetSpecFilename(String gadgetSpecFilename) {
        this.gadgetSpecFilename = gadgetSpecFilename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isOpensocial() {
        return opensocial;
    }

    public void setOpensocial(boolean opensocial) {
        this.opensocial = opensocial;
    }

    public boolean isOpensocial07() {
        return opensocial07;
    }

    public void setOpensocial07(boolean opensocial07) {
        this.opensocial07 = opensocial07;
    }

    public boolean isOpensocial08() {
        return opensocial08;
    }

    public void setOpensocial08(boolean opensocial08) {
        this.opensocial08 = opensocial08;
    }

    public boolean isOpensocial09() {
        return opensocial09;
    }

    public void setOpensocial09(boolean opensocial09) {
        this.opensocial09 = opensocial09;
    }

    public boolean isNonOpensocial() {
        return nonOpensocial;
    }

    public void setNonOpensocial(boolean nonOpensocial) {
        this.nonOpensocial = nonOpensocial;
    }

    public boolean isPubsub() {
        return pubsub;
    }

    public void setPubsub(boolean pubsub) {
        this.pubsub = pubsub;
    }

    public boolean isViews() {
        return views;
    }

    public void setViews(boolean views) {
        this.views = views;
    }

    public boolean isFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public boolean isDynamicHeight() {
        return dynamicHeight;
    }

    public void setDynamicHeight(boolean dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }

    public boolean isSetTitle() {
        return setTitle;
    }

    public void setSetTitle(boolean setTitle) {
        this.setTitle = setTitle;
    }

    public boolean isMiniMessage() {
        return miniMessage;
    }

    public void setMiniMessage(boolean miniMessage) {
        this.miniMessage = miniMessage;
    }

    public boolean isTabs() {
        return tabs;
    }

    public void setTabs(boolean tabs) {
        this.tabs = tabs;
    }

    public boolean isSkins() {
        return skins;
    }

    public void setSkins(boolean skins) {
        this.skins = skins;
    }

}
