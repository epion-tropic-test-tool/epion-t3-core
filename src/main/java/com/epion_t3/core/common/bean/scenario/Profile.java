/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * プロファイル（環境差分設定）.
 */
@Getter
@Setter
public class Profile implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 値管理.<br>
     * Key：プロファイル名<br>
     * Value：Key:Valueの定義（共に文字列定義のみ）
     */
    private Map<String, Map<String, String>> values;
}
