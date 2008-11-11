package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class GadgetXmlFileGenerator {
	
	private IProject project;
	
	private GadgetXmlData data;
	
	public GadgetXmlFileGenerator(IProject project, GadgetXmlData data) {
		super();
		this.project = project;
		this.data = data;
	}
	
	public IFile generate(IProgressMonitor monitor) throws UnsupportedEncodingException, CoreException {
		IFile gadgetXmlFile = project.getFile(new Path("gadget.xml"));
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<Module>\n";
		content += "  <ModulePrefs";
		content += " title=\"" + data.getTitle() + "\"";
		if (data.getTitleUrl().length() > 0) {
			content += " title_url=\"" + data.getTitleUrl() + "\"";
		}
		if (data.getAuthor().length() > 0) {
			content += " author=\"" + data.getAuthor() + "\"";
		}
		content += " author_email=\"" + data.getAuthorEmail() + "\"";
		if (data.getDescription().length() > 0) {
			content += " description=\"" + data.getDescription() + "\"";
		}
		if (data.getScreenshot().length() > 0) {
			content += " screenshot=\"" + data.getScreenshot() + "\"";
		}
		if (data.getThumbnail().length() > 0) {
			content += " thumbnail=\"" + data.getThumbnail() + "\"";
		}
		content += ">\n";
		if (data.isOpensocial08()) {
			content += "    <Require feature=\"opensocial-0.8\" />\n";
		}
		if (data.isOpensocial07()) {
			content += "    <Require feature=\"opensocial-0.7\" />\n";
		}
		if (data.isDynamicHeight()) {
			content += "    <Require feature=\"window\" />\n";
		}
		if (data.isFlash()) {
			content += "    <Require feature=\"flash\" />\n";
		}
		if (data.isMiniMessage()) {
			content += "    <Require feature=\"minimessage\" />\n";
		}
		if (data.isPubsub()) {
			content += "    <Require feature=\"pubsub\" />\n";
		}
		if (data.isSetTitle()) {
			content += "    <Require feature=\"settitle\" />\n";
		}
		if (data.isSkins()) {
			content += "    <Require feature=\"skins\" />\n";
		}
		if (data.isTabs()) {
			content += "    <Require feature=\"tabs\" />\n";
		}
		if (data.isViews()) {
			content += "    <Require feature=\"views\" />\n";
		}
		content += "  </ModulePrefs>\n";
		if (data.isCanvas()) {
			content += "  <Content type=\"html\" view=\"canvas\"><![CDATA[\n";
			content += "\n";
			content += "<!-- The code for canvas view is here. -->\n";
			content += "\n";
			content += "  ]]></Content>\n";
		}
		if (data.isProfile()) {
			content += "  <Content type=\"html\" view=\"profile\"><![CDATA[\n";
			content += "\n";
			content += "<!-- The code for profile view is here. -->\n";
			content += "\n";
			content += "  ]]></Content>\n";
		}
		if (data.isPreview()) {
			content += "  <Content type=\"html\" view=\"preview\"><![CDATA[\n";
			content += "\n";
			content += "<!-- The code for preview view is here. -->\n";
			content += "\n";
			content += "  ]]></Content>\n";
		}
		if (data.isHome()) {
			content += "  <Content type=\"html\" view=\"home\"><![CDATA[\n";
			content += "\n";
			content += "<!-- The code for home view is here. -->\n";
			content += "\n";
			content += "  ]]></Content>\n";
		}
		content += "</Module>";
		InputStream in = new ByteArrayInputStream(content.getBytes("UTF8"));
		gadgetXmlFile.create(in, false, monitor);
		return gadgetXmlFile;
	}

}
