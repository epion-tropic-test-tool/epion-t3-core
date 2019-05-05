package com.epion_t3.core.common.type;

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
