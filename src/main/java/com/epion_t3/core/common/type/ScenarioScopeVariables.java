/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScenarioScopeVariables {

    CURRENT_SCENARIO("currentScenario"),

    SCENARIO_DIR("scenarioDir"),

    EVIDENCE_DIR("evidenceDir"),

    /** 現在の繰り返し対象オブジェクト.
     * @since 0.0.3
     */
    CURRENT_ITERATE_TARGET("currentIterateTarget");

    private final String name;

}
