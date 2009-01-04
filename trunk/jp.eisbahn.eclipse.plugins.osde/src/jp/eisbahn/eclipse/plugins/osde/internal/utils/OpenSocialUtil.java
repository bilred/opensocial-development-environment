package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.google.gadgets.Module;

public class OpenSocialUtil {

	public static ApplicationInformation createApplicationId(IFile file) throws JAXBException, CoreException {
		try {
			JAXBContext context = JAXBContext.newInstance(Module.class);
			Unmarshaller um = context.createUnmarshaller();
			Module module = (Module)um.unmarshal(file.getContents());
			String path = file.getFullPath().toPortableString();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(path.getBytes("UTF-8"));
			String appId = Gadgets.toHexString(hash);
			ApplicationInformation info = new ApplicationInformation();
			info.setAppId(appId);
			info.setModule(module);
			return info;
		} catch(NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch(UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static boolean isGadgetXml(IFile file) {
		IContentTypeManager manager = Platform.getContentTypeManager();
		IContentType[] contentTypes = manager.findContentTypesFor(file.getLocation().toOSString());
		for (IContentType contentType : contentTypes) {
			if (contentType.getId().equals("jp.eisbahn.eclipse.plugins.osde.gadgetXml")) {
				return true;
			}
		}
		return false;
	}
	
}
