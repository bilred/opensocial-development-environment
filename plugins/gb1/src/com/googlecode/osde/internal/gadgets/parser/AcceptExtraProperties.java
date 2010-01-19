package com.googlecode.osde.internal.gadgets.parser;

import java.util.Map;

/**
 * Pojos that implements this interface accept extra xml attributes
 * other than its declared properties.
 *
 * @author Dolphin Wan
 */
public interface AcceptExtraProperties {
    Map<String, String> getExtraProperties();
}
