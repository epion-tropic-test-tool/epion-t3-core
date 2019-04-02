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

    CORE_ERR_0001("com.epion_t3.core.err.0001"),
    CORE_ERR_0002("com.epion_t3.core.err.0002"),
    CORE_ERR_0003("com.epion_t3.core.err.0003"),
    CORE_ERR_0004("com.epion_t3.core.err.0004"),
    CORE_ERR_0005("com.epion_t3.core.err.0005"),
    CORE_ERR_0006("com.epion_t3.core.err.0006"),
    CORE_ERR_0007("com.epion_t3.core.err.0007"),
    CORE_ERR_0008("com.epion_t3.core.err.0008"),
    CORE_ERR_0009("com.epion_t3.core.err.0009"),
    CORE_ERR_0010("com.epion_t3.core.err.0010"),
    CORE_ERR_0011("com.epion_t3.core.err.0011"),
    CORE_ERR_0012("com.epion_t3.core.err.0012"),
    CORE_ERR_0013("com.epion_t3.core.err.0013"),
    CORE_ERR_0014("com.epion_t3.core.err.0014"),
    CORE_ERR_0015("com.epion_t3.core.err.0015"),
    CORE_ERR_0016("com.epion_t3.core.err.0016"),
    CORE_ERR_0017("com.epion_t3.core.err.0017"),
    CORE_ERR_0018("com.epion_t3.core.err.0018"),
    CORE_ERR_0019("com.epion_t3.core.err.0019"),
    CORE_ERR_0020("com.epion_t3.core.err.0020"),
    CORE_ERR_0021("com.epion_t3.core.err.0021"),
    CORE_ERR_0022("com.epion_t3.core.err.0022"),
    CORE_ERR_1001("com.epion_t3.core.err.1001"),
    CORE_ERR_1002("com.epion_t3.core.err.1002"),
    CORE_ERR_1003("com.epion_t3.core.err.1003"),
    CORE_ERR_1004("com.epion_t3.core.err.1004"),
    CORE_ERR_1005("com.epion_t3.core.err.1005"),
    CORE_WRN_0001("com.epion_t3.core.wrn.0001"),
    CORE_WRN_0002("com.epion_t3.core.wrn.0002"),;

    /**
     * メッセージコード.
     */
    private String messageCode;
}
