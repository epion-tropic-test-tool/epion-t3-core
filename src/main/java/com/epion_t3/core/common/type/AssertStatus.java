package com.epion_t3.core.common.type;

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
