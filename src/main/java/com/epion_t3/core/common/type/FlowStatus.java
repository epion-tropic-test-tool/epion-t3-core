/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Flowステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FlowStatus {

    WAIT("table-secondary"),

    RUNNING("table-info"),

    CONTINUE("table-success"),

    BREAK("table-success"),

    FORCE_EXIT("table-warning"),

    SUCCESS("table-success"),

    ERROR("table-danger"),

    // ASSERT_ERROR("table-danger"),

    WARN("table-danger");

    private final String cssClass;

}
