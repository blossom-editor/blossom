package com.power.doc.model;


import com.power.doc.utils.StringUtils;

/**
 * @author xingzi
 * Date 2022/9/25 14:52
 */
public class SystemPlaceholders {

    public static final String PLACEHOLDER_PREFIX = "${";
    /**
     * Suffix for system property placeholders: "}".
     */
    public static final String PLACEHOLDER_SUFFIX = "}";
    /**
     * Value separator for system property placeholders: ":".
     */
    public static final String VALUE_SEPARATOR = ":";
    public static final String SIMPLE_PREFIX = "{";

    private SystemPlaceholders() {

    }

    public static boolean hasSystemProperties(String url) {
        return !StringUtils.isBlank(url) && url.contains(PLACEHOLDER_PREFIX) &&
            url.contains(PLACEHOLDER_SUFFIX)
            && url.contains(VALUE_SEPARATOR);
    }

    public static String replaceSystemProperties(String url) {
        return null;
    }
}
