package com.google.gadgets.impl;

import com.google.gadgets.ModuleDocument;

import junit.framework.TestCase;

public class GadgetXmlLoadTest extends TestCase {
	
	public void testGadgetXMLファイルの読み込み() throws Exception {
		ModuleDocument moduleDocument = ModuleDocument.Factory.parse("retrieve_it.xml");
		
	}

}
