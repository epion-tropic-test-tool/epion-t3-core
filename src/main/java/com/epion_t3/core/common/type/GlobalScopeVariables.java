/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GlobalScopeVariables {

    ROOT_DIR("rootDir"),

    SCENARIO_DIR("scenarioDir"),

    SCENARIO_EVIDENCE_DIR("scenarioDir"),

    WEB_ASSETS_ROOT("webAssertsRoot"),;

    private final String name;

}
