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
    CORE_INF_0001("com.zomu.t.epion.t3.core.inf.0001"),
    CORE_ERR_0001("com.zomu.t.epion.t3.core.err.0001"),
    CORE_ERR_0002("com.zomu.t.epion.t3.core.err.0002"),
    CORE_ERR_0003("com.zomu.t.epion.t3.core.err.0003"),
    CORE_ERR_0004("com.zomu.t.epion.t3.core.err.0004"),
    CORE_ERR_0005("com.zomu.t.epion.t3.core.err.0005"),
    CORE_ERR_0006("com.zomu.t.epion.t3.core.err.0006"),
    CORE_ERR_0007("com.zomu.t.epion.t3.core.err.0007"),
    CORE_ERR_0008("com.zomu.t.epion.t3.core.err.0008"),
    CORE_ERR_0009("com.zomu.t.epion.t3.core.err.0009"),
    CORE_ERR_0010("com.zomu.t.epion.t3.core.err.0010"),
    CORE_ERR_0011("com.zomu.t.epion.t3.core.err.0011"),
    CORE_ERR_0012("com.zomu.t.epion.t3.core.err.0012"),
    CORE_ERR_0013("com.zomu.t.epion.t3.core.err.0013"),
    CORE_ERR_0014("com.zomu.t.epion.t3.core.err.0014"),
    CORE_ERR_0015("com.zomu.t.epion.t3.core.err.0015"),
    CORE_ERR_0016("com.zomu.t.epion.t3.core.err.0016"),
    CORE_ERR_0017("com.zomu.t.epion.t3.core.err.0017"),
    CORE_ERR_0018("com.zomu.t.epion.t3.core.err.0018"),
    CORE_ERR_0019("com.zomu.t.epion.t3.core.err.0019"),
    CORE_ERR_0020("com.zomu.t.epion.t3.core.err.0020"),
    CORE_ERR_0021("com.zomu.t.epion.t3.core.err.0021"),
    CORE_ERR_0022("com.zomu.t.epion.t3.core.err.0022"),
    CORE_ERR_0023("com.zomu.t.epion.t3.core.err.0023"),
    CORE_ERR_0024("com.epion_t3.core.err.0024"),
    CORE_ERR_0025("com.epion_t3.core.err.0025"),
    CORE_ERR_0026("com.epion_t3.core.err.0026"),
    CORE_ERR_0027("com.epion_t3.core.err.0027"),
    CORE_ERR_0028("com.epion_t3.core.err.0028"),
    CORE_ERR_0029("com.epion_t3.core.err.0029"),
    CORE_ERR_0030("com.epion_t3.core.err.0030"),
    CORE_ERR_0031("com.epion_t3.core.err.0031"),
    CORE_ERR_0032("com.epion_t3.core.err.0032"),
    CORE_ERR_0033("com.epion_t3.core.err.0033"),
    CORE_ERR_0034("com.epion_t3.core.err.0034"),
    CORE_ERR_0035("com.epion_t3.core.err.0035"),
    CORE_ERR_0036("com.epion_t3.core.err.0036"),
    CORE_ERR_0037("com.epion_t3.core.err.0037"),
    CORE_ERR_0038("com.epion_t3.core.err.0038"),
    CORE_ERR_0039("com.epion_t3.core.err.0039"),
    CORE_ERR_0040("com.epion_t3.core.err.0040"),
    CORE_ERR_0041("com.epion_t3.core.err.0041"),
    CORE_ERR_0042("com.epion_t3.core.err.0042"),
    CORE_ERR_0043("com.epion_t3.core.err.0043"),
    CORE_ERR_0044("com.epion_t3.core.err.0044"),
    CORE_ERR_1001("com.zomu.t.epion.t3.core.err.1001"),
    CORE_ERR_1002("com.zomu.t.epion.t3.core.err.1002"),
    CORE_ERR_1003("com.zomu.t.epion.t3.core.err.1003"),
    CORE_ERR_1004("com.zomu.t.epion.t3.core.err.1004"),
    CORE_ERR_1005("com.zomu.t.epion.t3.core.err.1005"),
    CORE_WRN_0001("com.zomu.t.epion.t3.core.wrn.0001"),
    CORE_WRN_0002("com.zomu.t.epion.t3.core.wrn.0002"),
    CORE_WRN_0003("com.epion_t3.core.wrn.0003"),
    ;

    /**
     * メッセージコード.
     */
    private String messageCode;
}
