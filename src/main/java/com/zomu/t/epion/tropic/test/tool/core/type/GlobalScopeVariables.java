package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalScopeVariables {


    ROOT_DIR("rootDir"),

    SCENARIO_DIR("scenarioDir"),

    SCENARIO_EVIDENCE_DIR("scenarioDir"),
    ;

    private String name;

}
