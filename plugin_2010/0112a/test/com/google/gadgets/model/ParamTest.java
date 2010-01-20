package com.google.gadgets.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ParamTest {

    private Param param;

    @Before
    public void setUp() throws Exception {
        param = new Param();
        param.setName("blah");
        param.setValue("blah blah");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGetValue() {
        assertTrue("blah blah".equals(param.getValue()));
    }

    @Test
    public final void testGetName() {
        assertTrue("blah".equals(param.getName()));
    }

}
