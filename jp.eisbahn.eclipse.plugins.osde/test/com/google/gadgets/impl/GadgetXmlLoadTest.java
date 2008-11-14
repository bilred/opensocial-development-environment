package com.google.gadgets.impl;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.gadgets.Module;

import junit.framework.TestCase;

public class GadgetXmlLoadTest extends TestCase {
	
	public void testGadgetXMLファイルの読み込み() throws Exception {
		JAXBContext context = JAXBContext.newInstance(Module.class);
		Unmarshaller um = context.createUnmarshaller();
		Module module = (Module)um.unmarshal(new File("test/retrieve_it.xml"));
		Marshaller ma = context.createMarshaller();
		ma.marshal(module, new File("test/retrieve_it_new.xml"));
	}

}
