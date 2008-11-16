package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.google.gadgets.ViewName;
import com.google.gadgets.ViewType;

public class GadgetXmlFileGenerator {
	
	private IProject project;
	
	private GadgetXmlData gadgetXmlData;
	
	private EnumMap<ViewName, GadgetViewData> gadgetViewData;
	
	public GadgetXmlFileGenerator(IProject project, GadgetXmlData gadgetXmlData, EnumMap<ViewName, GadgetViewData> gadgetViewData) {
		super();
		this.project = project;
		this.gadgetXmlData = gadgetXmlData;
		this.gadgetViewData = gadgetViewData;
	}
	
	public IFile generate(IProgressMonitor monitor) throws UnsupportedEncodingException, CoreException {
		IFile gadgetXmlFile = project.getFile(new Path("gadget.xml"));
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		content += "<Module>\n";
		content += "  <ModulePrefs";
		content += " title=\"" + gadgetXmlData.getTitle() + "\"";
		if (gadgetXmlData.getTitleUrl().length() > 0) {
			content += " title_url=\"" + gadgetXmlData.getTitleUrl() + "\"";
		}
		if (gadgetXmlData.getAuthor().length() > 0) {
			content += " author=\"" + gadgetXmlData.getAuthor() + "\"";
		}
		content += " author_email=\"" + gadgetXmlData.getAuthorEmail() + "\"";
		if (gadgetXmlData.getDescription().length() > 0) {
			content += " description=\"" + gadgetXmlData.getDescription() + "\"";
		}
		if (gadgetXmlData.getScreenshot().length() > 0) {
			content += " screenshot=\"" + gadgetXmlData.getScreenshot() + "\"";
		}
		if (gadgetXmlData.getThumbnail().length() > 0) {
			content += " thumbnail=\"" + gadgetXmlData.getThumbnail() + "\"";
		}
		content += ">\n";
		if (gadgetXmlData.isOpensocial08()) {
			content += "    <Require feature=\"opensocial-0.8\" />\n";
		}
		if (gadgetXmlData.isOpensocial07()) {
			content += "    <Require feature=\"opensocial-0.7\" />\n";
		}
		if (gadgetXmlData.isDynamicHeight()) {
			content += "    <Require feature=\"window\" />\n";
		}
		if (gadgetXmlData.isFlash()) {
			content += "    <Require feature=\"flash\" />\n";
		}
		if (gadgetXmlData.isMiniMessage()) {
			content += "    <Require feature=\"minimessage\" />\n";
		}
		if (gadgetXmlData.isPubsub()) {
			content += "    <Require feature=\"pubsub\" />\n";
		}
		if (gadgetXmlData.isSetTitle()) {
			content += "    <Require feature=\"settitle\" />\n";
		}
		if (gadgetXmlData.isSkins()) {
			content += "    <Require feature=\"skins\" />\n";
		}
		if (gadgetXmlData.isTabs()) {
			content += "    <Require feature=\"tabs\" />\n";
		}
		if (gadgetXmlData.isViews()) {
			content += "    <Require feature=\"views\" />\n";
		}
		content += "  </ModulePrefs>\n";
		Set<ViewName> keySet = gadgetViewData.keySet();
		for (ViewName viewName : keySet) {
			GadgetViewData viewData = gadgetViewData.get(viewName);
			if (viewData.getType().equals(ViewType.HTML)) {
				content += "  <Content type=\"html\" view=\"" + viewName.toString() + "\"><![CDATA[\n";
				if (viewData.isCreateExternalJavaScript()) {
					content += "\n";
					content += "<script type=\"text/javascript\" src=\"http://your.server.host/" + viewName.toString() + ".js\"></script>\n";
					if (viewData.isCreateInitFunction()) {
						content += "\n";
						content += "<script type=\"text/javascript\">\n";
						content += "gadgets.util.registerOnLoadHandler(\"init\");\n";
						content += "</script>\n";
					}
				}
				content += "\n";
				content += "<!-- The code for " + viewName.getDisplayName() + " view is here. -->\n";
				content += "\n";
				content += "  ]]></Content>\n";
			} else if (viewData.getType().equals(ViewType.URL)) {
				content += "  <Content type=\"url\" view=\"" + viewName.toString() + "\" href=\"" + viewData.getHref() + "\" />\n";
			}
		}
		content += "</Module>";
		InputStream in = new ByteArrayInputStream(content.getBytes("UTF8"));
		gadgetXmlFile.create(in, false, monitor);
		return gadgetXmlFile;
	}

}
