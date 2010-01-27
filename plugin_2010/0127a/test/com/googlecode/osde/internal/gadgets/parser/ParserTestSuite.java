package com.googlecode.osde.internal.gadgets.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ParserTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.googlecode.osde.internal.gadgets.parser");

        suite.addTest(new GadgetXMLParserTest("testInitialize"));
        suite.addTest(new GadgetXMLParserTest("testParse"));
        suite.addTest(new MessageBundleXMLParserTest("testParse"));
        suite.addTest(new ParserFactoryTest("testCreateParser"));

        return suite;
    }

}
