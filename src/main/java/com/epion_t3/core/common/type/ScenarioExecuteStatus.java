package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * シナリオ実行ステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum ScenarioExecuteStatus {

    WAIT("table-secondary"),

    SKIP("table-secondary"),

    RUNNING("table-info"),

    SUCCESS("table-success"),

    ERROR("table-danger"),

    ASSERT_ERROR("table-danger");

    private String cssClass;

}
