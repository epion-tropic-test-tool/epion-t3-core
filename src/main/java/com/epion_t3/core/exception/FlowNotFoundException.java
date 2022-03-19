/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * Flowが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class FlowNotFoundException extends SystemException {

    /** FlowId. */
    private String flowId;

    /**
     * コンストラクタ.
     * 
     * @param flowId FlowId
     */
    public FlowNotFoundException(String flowId) {
        super("not found flow: '" + flowId + "'");
        this.flowId = flowId;
    }
}
