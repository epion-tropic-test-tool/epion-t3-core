/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.message.impl;

import com.epion_t3.core.message.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * コアメッセージ.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum CoreMessages implements Messages {
    CORE_INF_0001("com.epion_t3.core.inf.0001"), CORE_ERR_0001("com.epion_t3.core.err.0001"),
    CORE_ERR_0002("com.epion_t3.core.err.0002"), CORE_ERR_0003("com.epion_t3.core.err.0003"),
    CORE_ERR_0004("com.epion_t3.core.err.0004"), CORE_ERR_0005("com.epion_t3.core.err.0005"),
    CORE_ERR_0006("com.epion_t3.core.err.0006"), CORE_ERR_0007("com.epion_t3.core.err.0007"),
    CORE_ERR_0008("com.epion_t3.core.err.0008"), CORE_ERR_0009("com.epion_t3.core.err.0009"),
    CORE_ERR_0010("com.epion_t3.core.err.0010"), CORE_ERR_0011("com.epion_t3.core.err.0011"),
    CORE_ERR_0012("com.epion_t3.core.err.0012"), CORE_ERR_0013("com.epion_t3.core.err.0013"),
    CORE_ERR_0014("com.epion_t3.core.err.0014"), CORE_ERR_0015("com.epion_t3.core.err.0015"),
    CORE_ERR_0016("com.epion_t3.core.err.0016"), CORE_ERR_0017("com.epion_t3.core.err.0017"),
    CORE_ERR_0018("com.epion_t3.core.err.0018"), CORE_ERR_0019("com.epion_t3.core.err.0019"),
    CORE_ERR_0020("com.epion_t3.core.err.0020"), CORE_ERR_0021("com.epion_t3.core.err.0021"),
    CORE_ERR_0022("com.epion_t3.core.err.0022"), CORE_ERR_0023("com.epion_t3.core.err.0023"),
    CORE_ERR_0024("com.epion_t3.core.err.0024"), CORE_ERR_0025("com.epion_t3.core.err.0025"),
    CORE_ERR_0026("com.epion_t3.core.err.0026"), CORE_ERR_0027("com.epion_t3.core.err.0027"),
    CORE_ERR_0028("com.epion_t3.core.err.0028"), CORE_ERR_0029("com.epion_t3.core.err.0029"),
    CORE_ERR_0030("com.epion_t3.core.err.0030"), CORE_ERR_0031("com.epion_t3.core.err.0031"),
    CORE_ERR_0032("com.epion_t3.core.err.0032"), CORE_ERR_0033("com.epion_t3.core.err.0033"),
    CORE_ERR_0034("com.epion_t3.core.err.0034"), CORE_ERR_0035("com.epion_t3.core.err.0035"),
    CORE_ERR_0036("com.epion_t3.core.err.0036"), CORE_ERR_0037("com.epion_t3.core.err.0037"),
    CORE_ERR_0038("com.epion_t3.core.err.0038"), CORE_ERR_0039("com.epion_t3.core.err.0039"),
    CORE_ERR_0040("com.epion_t3.core.err.0040"), CORE_ERR_0041("com.epion_t3.core.err.0041"),
    CORE_ERR_0042("com.epion_t3.core.err.0042"), CORE_ERR_0043("com.epion_t3.core.err.0043"),
    CORE_ERR_0044("com.epion_t3.core.err.0044"), CORE_ERR_0045("com.epion_t3.core.err.0045"),
    CORE_ERR_0046("com.epion_t3.core.err.0046"), CORE_ERR_0047("com.epion_t3.core.err.0047"),
    CORE_ERR_0048("com.epion_t3.core.err.0048"), CORE_ERR_0049("com.epion_t3.core.err.0049"),
    CORE_ERR_0050("com.epion_t3.core.err.0050"), CORE_ERR_0051("com.epion_t3.core.err.0051"),
    CORE_ERR_0052("com.epion_t3.core.err.0052"), CORE_ERR_0053("com.epion_t3.core.err.0053"),
    CORE_ERR_0054("com.epion_t3.core.err.0054"), CORE_ERR_0055("com.epion_t3.core.err.0055"),
    CORE_ERR_0056("com.epion_t3.core.err.0056"), CORE_ERR_0057("com.epion_t3.core.err.0057"),
    CORE_ERR_0058("com.epion_t3.core.err.0058"), CORE_ERR_0059("com.epion_t3.core.err.0059"),
    CORE_ERR_0060("com.epion_t3.core.err.0060"), CORE_ERR_0061("com.epion_t3.core.err.0061"),
    CORE_ERR_0062("com.epion_t3.core.err.0062"), CORE_ERR_0063("com.epion_t3.core.err.0063"),
    CORE_ERR_0064("com.epion_t3.core.err.0064"), CORE_ERR_0065("com.epion_t3.core.err.0065"),
    CORE_ERR_0066("com.epion_t3.core.err.0066"), CORE_ERR_0067("com.epion_t3.core.err.0067"),
    CORE_ERR_0068("com.epion_t3.core.err.0068"), CORE_ERR_0069("com.epion_t3.core.err.0069"),
    CORE_ERR_0070("com.epion_t3.core.err.0070"), CORE_ERR_0071("com.epion_t3.core.err.0071"),
    CORE_ERR_0072("com.epion_t3.core.err.0072"), CORE_ERR_0073("com.epion_t3.core.err.0073"),
    CORE_ERR_0074("com.epion_t3.core.err.0074"), CORE_ERR_1001("com.epion_t3.core.err.1001"),
    CORE_ERR_1002("com.epion_t3.core.err.1002"), CORE_ERR_1003("com.epion_t3.core.err.1003"),
    CORE_ERR_1004("com.epion_t3.core.err.1004"), CORE_ERR_1005("com.epion_t3.core.err.1005"),
    CORE_WRN_0001("com.epion_t3.core.wrn.0001"), CORE_WRN_0002("com.epion_t3.core.wrn.0002"),
    CORE_WRN_0003("com.epion_t3.core.wrn.0003"), CORE_WRN_0004("com.epion_t3.core.wrn.0004"),;

    /**
     * メッセージコード.
     */
    private String messageCode;
}
