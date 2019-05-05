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

    private String cssClass;

}
