/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 変数.
 */
@Getter
@Setter
public class Variable implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * グローバル変数.
     */
    private Map<String, Object> global;

    /**
     * シナリオ変数.
     */
    private Map<String, Object> scenario;

}
