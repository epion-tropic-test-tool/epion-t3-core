/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * While系の親Flowクラス.
 */
@Getter
@Setter
public abstract class AbstractWhileFlow extends HasChildrenFlow {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * タイムアウト
     */
    @NotNull
    private Long timeout = 30000L;

}
