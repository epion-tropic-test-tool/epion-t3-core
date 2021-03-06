/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum InitializeArgs {

    OUTPUT_ROOT_PATH("o", "output", true, "template output root path.", true),

    INIT("i", "init", false, "initialize flag.", true);

    /**
     * 短いオプション名.
     */
    private final String shortName;

    /**
     * 長いオプション名.
     */
    private final String longName;

    /**
     * 引数の値をとるかどうかの指定.
     */
    private final boolean hasArg;

    /**
     * 説明.
     */
    private final String description;

    /**
     * 引数が必須かどうかの指定.
     */
    private final boolean required;

}
