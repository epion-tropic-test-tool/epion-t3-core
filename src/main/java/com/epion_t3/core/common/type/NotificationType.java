package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    INFO("table-secondary"),

    WARN("table-warning"),

    ERROR("table-danger");

    private String cssClass;

}
