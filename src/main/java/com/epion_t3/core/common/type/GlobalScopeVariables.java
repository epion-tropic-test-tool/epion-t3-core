package com.epion_t3.core.common.type;

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
