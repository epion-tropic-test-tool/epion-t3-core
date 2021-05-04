/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Args {

    // XXX: 暫定対処
    // 現状あまりにも利用用途がないため、不要とする.（復活はしないかも）
    // VERSION("v", "version", true, "run tool version.", true),

    SCENARIO("t", "target", true, "target of tool run.", true),

    ROOT_PATH("s", "com/epion_t3/core/common/bean/scenario", true, "scenario root path.", false),

    RESULT_ROOT_PATH("o", "output", true, "result output root path.", false),

    FILESYSTEM_KIND("f", "filesystem", true, "scenario manage filesystem kind.", false),

    PROFILE("p", "profile", true, "profile of tool run.", false),

    MODE("m", "mode", true, "mode of tool run.", false),

    DEBUG("d", "debug", false, "run tool for debug.", false),

    /**
     * Disabled Recommendation Start...
     *
     * @since 0.0.5
     */
    NO_REPORT("n", "noreport", false, "no report output. (migrate to config file in the future)", false),

    /**
     * Disabled Recommendation Start...
     *
     * @since 0.0.5
     */
    CONSOLE_REPORT("c", "console", false, "output console report. (migrate to config file in the future)", false),

    /**
     * Disabled Recommendation Start...
     *
     * @since 0.0.5
     */
    WEB_ASSET_PATH("a", "webassets", true, "web assets base path. (migrate to config file in the future)", false),

    HELP("h", "help", true, "show the tool help.", false),

    /**
     * 設定ファイルパス.
     *
     * @since 0.0.5
     */
    CONFIG("c", "config", true, "config file path.", false);

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
