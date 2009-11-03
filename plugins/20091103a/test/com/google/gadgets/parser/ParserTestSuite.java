package com.google.gadgets.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ParserTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.google.gadgets.parser");
		
		suite.addTest(new GadgetXMLParserTest("testInitialize"));
		suite.addTest(new GadgetXMLParserTest("testParse"));
		suite.addTest(new MessageBundleXMLParserTest("testParse"));
		suite.addTest(new ParserFactoryTest("testCreateParser"));

		return suite;
	}

}
