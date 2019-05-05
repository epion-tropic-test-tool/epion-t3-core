package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScenarioScopeVariables {

    CURRENT_SCENARIO("currentScenario"),

    SCENARIO_DIR("scenarioDir"),

    EVIDENCE_DIR("evidenceDir");

    private String name;

}
