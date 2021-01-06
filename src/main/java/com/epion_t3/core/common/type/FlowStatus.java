/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Flowステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum FlowStatus {

    WAIT("table-secondary"),

    SKIP("table-secondary"),

    RUNNING("table-info"),

    SUCCESS("table-success"),

    ERROR("table-danger"),

    ASSERT_ERROR("table-danger"),

    WARN("table-danger");

    private final String cssClass;

}
