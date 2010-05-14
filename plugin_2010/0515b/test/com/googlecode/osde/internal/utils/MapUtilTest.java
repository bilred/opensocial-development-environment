package com.googlecode.osde.internal.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

public class MapUtilTest {

    @Test
    public void testConvert() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(RandomStringUtils.randomAscii(10), RandomStringUtils.randomAscii(10));

        Map<String, String> result = MapUtil.toMap(MapUtil.toString(map));
        Assert.assertEquals(map, result);
    }

    @Test
    public void testNull() throws Exception {
        Assert.assertEquals(
                MapUtil.toString(new HashMap<String, String>()), MapUtil.toString(null));
        Assert.assertEquals(new HashMap<String, String>(), MapUtil.toMap(null));
        Assert.assertEquals(new HashMap<String, String>(), MapUtil.toMap(""));
    }
}
