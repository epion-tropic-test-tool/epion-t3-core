/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.common.annotation.OriginalFlowField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

/**
 * Flow.
 *
 * @author takashno
 */
@Getter
@Setter
public class Flow implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @OriginalFlowField
    @NotEmpty
    private String id;

    /**
     * Flow種別.
     */
    @OriginalFlowField
    @NotEmpty
    private String type;

    /**
     * 概要.
     */
    @OriginalFlowField
    private String summary;

}
