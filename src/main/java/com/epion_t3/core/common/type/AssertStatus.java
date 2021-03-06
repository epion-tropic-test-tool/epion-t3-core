/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AssertStatus {

    WAIT("table-secondary"),

    RUNNING("table-info"),

    OK("table-success"),

    NG("table-danger"),

    WARN("table-danger");

    private final String cssClass;

}
