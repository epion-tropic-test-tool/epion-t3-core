/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Scenario implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 実行シナリオ.
     */
    private String ref;

    /**
     * プロファイル指定.
     */
    private String profile;

    /**
     * 実行モード
     */
    private String mode;

    /**
     * レポート出力しない
     */
    private Boolean noreport;

    /**
     * デバッグ指定
     */
    private Boolean debug;

}
