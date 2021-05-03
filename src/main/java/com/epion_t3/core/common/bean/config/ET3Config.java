/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.config;

import com.epion_t3.core.common.type.PathResolveMode;
import lombok.Getter;
import lombok.Setter;

/**
 * ETTTの設定ファイル.
 *
 * @since 0.0.5
 */
@Getter
@Setter
public class ET3Config {

    /** 実行モード（現状「test」のみだが…）. */
    private String mode;

    /** シナリオ配置のルートパス. */
    private String scenarioRootPath;

    /** 結果配置のルートパス. */
    private String resultRootPath;

    /** プロファイル. */
    private String profile;

    /** デバッグ指定. */
    private boolean debug = false;

    /** レポート出力有無. */
    private boolean noReport = false;

    /** コンソール出力版のレポートを出力するかの指定. */
    private boolean consoleReport = false;

    /** WEBアセットの参照パス. */
    private String webAssetPath;

    /** パス解決モード. */
    private String pathResolveMode;

}
