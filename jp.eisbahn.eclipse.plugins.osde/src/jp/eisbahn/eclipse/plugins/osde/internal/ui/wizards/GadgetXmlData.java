package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.Serializable;

/**
 * Gadget XMLファイルの情報を持つクラスです。
 * @author yoichiro
 */
public class GadgetXmlData implements Serializable {

	/** Title */
	private String title;
	/** Title URL */
	private String titleUrl;
	/** Description */
	private String description;
	/** Author */
	private String author;
	/** Author Email */
	private String authorEmail;
	/** Screenshot */
	private String screenshot;
	/** Thumbnail */
	private String thumbnail;
	/** opensocial-0.8 */
	private boolean opensocial08;
	/** opensocial-0.7 */
	private boolean opensocial07;
	/** PubSub */
	private boolean pubsub;
	/** Views */
	private boolean views;
	/** Flash */
	private boolean flash;
	/** Dynamic Height */
	private boolean dynamicHeight;
	/** Set Title */
	private boolean setTitle;
	/** Skins */
	private boolean skins;
	/** Mini Message */
	private boolean miniMessage;
	/** Tabs */
	private boolean tabs;
	/** Canvas */
	private boolean canvas;
	/** Profile */
	private boolean profile;
	/** Preview */
	private boolean preview;
	/** Home */
	private boolean home;
	
	/**
	 * Titleを返します。
	 * @return title Title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Titleをセットします。
	 * @param title Title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * TitleUrlを返します。
	 * @return titleUrl TitleUrl
	 */
	public String getTitleUrl() {
		return titleUrl;
	}
	
	/**
	 * TitleUrlをセットします。
	 * @param titleUrl TitleUrl
	 */
	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}
	
	/**
	 * Descriptionを返します。
	 * @return description Description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Descriptionをセットします。
	 * @param description Description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Authorを返します。
	 * @return author Author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Authorをセットします。
	 * @param author Author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * AuthorEmailを返します。
	 * @return authorEmail AuthorEmail
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}
	
	/**
	 * AuthorEmailをセットします。
	 * @param authorEmail AuthorEmail
	 */
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	
	/**
	 * Screenshotを返します。
	 * @return screenshot Screenshot
	 */
	public String getScreenshot() {
		return screenshot;
	}
	
	/**
	 * Screenshotをセットします。
	 * @param screenshot Screenshot
	 */
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}
	
	/**
	 * Thumbnailを返します。
	 * @return thumbnail Thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}
	
	/**
	 * Thumbnailをセットします。
	 * @param thumbnail Thumbnail
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	/**
	 * opensocial-0.8を返します。
	 * @return opensocial-0.8 opensocial-0.8
	 */
	public boolean isOpensocial08() {
		return opensocial08;
	}
	
	/**
	 * opensocial-0.8をセットします。
	 * @param opensocial08 opensocial-0.8
	 */
	public void setOpensocial08(boolean opensocial08) {
		this.opensocial08 = opensocial08;
	}
	
	/**
	 * opensocial-0.7を返します。
	 * @return opensocial07 opensocial-0.7
	 */
	public boolean isOpensocial07() {
		return opensocial07;
	}
	
	/**
	 * opensocial-0.7をセットします。
	 * @param opensocial07 opensocial-0.7
	 */
	public void setOpensocial07(boolean opensocial07) {
		this.opensocial07 = opensocial07;
	}
	
	/**
	 * PubSubを返します。
	 * @return pubsub PubSub
	 */
	public boolean isPubsub() {
		return pubsub;
	}
	
	/**
	 * PubSubをセットします。
	 * @param pubsub PubSub
	 */
	public void setPubsub(boolean pubsub) {
		this.pubsub = pubsub;
	}
	
	/**
	 * Viewsを返します。
	 * @return views Views
	 */
	public boolean isViews() {
		return views;
	}
	
	/**
	 * Viewsをセットします。
	 * @param views Views
	 */
	public void setViews(boolean views) {
		this.views = views;
	}
	
	/**
	 * Flashを返します。
	 * @return flash Flash
	 */
	public boolean isFlash() {
		return flash;
	}
	
	/**
	 * Flashをセットします。
	 * @param flash Flash
	 */
	public void setFlash(boolean flash) {
		this.flash = flash;
	}
	
	/**
	 * Dynamic Heightを返します。
	 * @return dynamicHeight Dynamic Height
	 */
	public boolean isDynamicHeight() {
		return dynamicHeight;
	}
	
	/**
	 * Dynamic Heightをセットします。
	 * @param dynamicHeight Dynamic Height
	 */
	public void setDynamicHeight(boolean dynamicHeight) {
		this.dynamicHeight = dynamicHeight;
	}
	
	/**
	 * Set Titleを返します。
	 * @return setTitle Set Title
	 */
	public boolean isSetTitle() {
		return setTitle;
	}
	
	/**
	 * Set Titleをセットします。
	 * @param setTitle Set Title
	 */
	public void setSetTitle(boolean setTitle) {
		this.setTitle = setTitle;
	}
	
	/**
	 * Mini Messageを返します。
	 * @return miniMessage Mini Message
	 */
	public boolean isMiniMessage() {
		return miniMessage;
	}
	
	/**
	 * Mini Messageをセットします。
	 * @param miniMessage Mini Message
	 */
	public void setMiniMessage(boolean miniMessage) {
		this.miniMessage = miniMessage;
	}
	
	/**
	 * Tabsを返します。
	 * @return tabs Tabs
	 */
	public boolean isTabs() {
		return tabs;
	}
	
	/**
	 * Tabsをセットします。
	 * @param tabs Tabs
	 */
	public void setTabs(boolean tabs) {
		this.tabs = tabs;
	}
	
	/**
	 * Canvasを返します。
	 * @return canvas Canvas
	 */
	public boolean isCanvas() {
		return canvas;
	}
	
	/**
	 * Canvasをセットします。
	 * @param canvas Canvas
	 */
	public void setCanvas(boolean canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * Profileを返します。
	 * @return profile Profile
	 */
	public boolean isProfile() {
		return profile;
	}
	
	/**
	 * Profileをセットします。
	 * @param profile Profile
	 */
	public void setProfile(boolean profile) {
		this.profile = profile;
	}
	
	/**
	 * Previewを返します。
	 * @return preview Preview
	 */
	public boolean isPreview() {
		return preview;
	}
	
	/**
	 * Previewをセットします。
	 * @param preview Preview
	 */
	public void setPreview(boolean preview) {
		this.preview = preview;
	}
	
	/**
	 * Homeを返します。
	 * @return home Home
	 */
	public boolean isHome() {
		return home;
	}
	
	/**
	 * Homeをセットします。
	 * @param home Home
	 */
	public void setHome(boolean home) {
		this.home = home;
	}

	/**
	 * Skinsを返します。
	 * @return skins Skins
	 */
	public boolean isSkins() {
		return skins;
	}

	/**
	 * Skinsをセットします。
	 * @param skins Skins
	 */
	public void setSkins(boolean skins) {
		this.skins = skins;
	}
	
}
