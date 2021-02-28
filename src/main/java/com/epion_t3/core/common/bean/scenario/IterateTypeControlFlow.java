/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

/**
 * 繰り返し系のFlowを制御するFlowの基底クラス. ・Break ・Continue
 */
@Getter
@Setter
public class IterateTypeControlFlow extends Flow {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 対象とするFlowID
     */
    @NotEmpty
    private String target;

}
