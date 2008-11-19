package jp.eisbahn.eclipse.plugins.osde.internal.editors;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

import com.google.gadgets.Module;

public class GadgetXmlDescriber implements IContentDescriber {

	public int describe(InputStream contents, IContentDescription description) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(Module.class);
			Unmarshaller um = context.createUnmarshaller();
			um.unmarshal(contents);
			return IContentDescriber.VALID;
		} catch (JAXBException e) {
			return IContentDescriber.INDETERMINATE;
		}
		
	}

	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

}
