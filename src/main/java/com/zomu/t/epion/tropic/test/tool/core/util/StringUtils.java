package com.zomu.t.epion.tropic.test.tool.core.util;

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
