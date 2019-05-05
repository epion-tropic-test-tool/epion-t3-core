package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 処理ステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum CommandStatus {

    WAIT("table-secondary"),

    RUNNING("table-info"),

    SUCCESS("table-success"),

    ERROR("table-danger");

    private String cssClass;

}
