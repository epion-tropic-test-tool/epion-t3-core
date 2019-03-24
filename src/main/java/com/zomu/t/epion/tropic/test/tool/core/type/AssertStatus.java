package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssertStatus {

    WAIT("table-secondary"),

    RUNNING("table-info"),

    OK("table-success"),

    NG("table-danger"),

    WARN("table-danger");

    private String cssClass;

}
