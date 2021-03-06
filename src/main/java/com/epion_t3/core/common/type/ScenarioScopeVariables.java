/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ScenarioScopeVariables {

    CURRENT_SCENARIO("currentScenario"),

    SCENARIO_DIR("scenarioDir"),

    EVIDENCE_DIR("evidenceDir"),

    /**
     * 現在の繰り返し対象オブジェクト.
     * 
     * @since 0.0.4
     */
    CURRENT_ITERATE_TARGET("currentIterateTarget");

    private final String name;

}
