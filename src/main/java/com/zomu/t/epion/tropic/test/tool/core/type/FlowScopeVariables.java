package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FlowScopeVariables {

    CURRENT_FLOW("currentFlow"),

    CURRENT_FLOW_EXECUTE_ID("currentFlowExecuteId"),

    CURRENT_COMMAND("currentCommand"),

    CURRENT_COMMAND_EXECUTE_ID("currentProcessExecuteId"),;

    private String name;

}
