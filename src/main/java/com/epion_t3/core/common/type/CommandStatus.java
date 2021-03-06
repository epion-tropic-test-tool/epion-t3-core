/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 処理ステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommandStatus {

    WAIT("table-secondary"),

    RUNNING("table-info"),

    SUCCESS("table-success"),

    ERROR("table-danger");

    private final String cssClass;

}
