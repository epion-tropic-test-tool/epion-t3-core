package com.epion_t3.core.common.util;

public final class StringUtils {

    private static final StringUtils instance = new StringUtils();

    private StringUtils() {

    }

    public static StringUtils getInstance() {
        return instance;
    }

    public String prettyHtml(String target) {
        return target.replaceAll("\\n", "<br/>").replaceAll(" ", "&nbsp;");
    }

}
