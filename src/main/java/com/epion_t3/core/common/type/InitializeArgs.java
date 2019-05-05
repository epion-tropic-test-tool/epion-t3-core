package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InitializeArgs {

    OUTPUT_ROOT_PATH("o", "output", true, "template output root path.", true),

    INIT("i", "init", false, "initialize flag.", true);

    /**
     * 短いオプション名.
     */
    private String shortName;

    /**
     * 長いオプション名.
     */
    private String longName;

    /**
     * 引数の値をとるかどうかの指定.
     */
    private boolean hasArg;

    /**
     * 説明.
     */
    private String description;

    /**
     * 引数が必須かどうかの指定.
     */
    private boolean required;

}
