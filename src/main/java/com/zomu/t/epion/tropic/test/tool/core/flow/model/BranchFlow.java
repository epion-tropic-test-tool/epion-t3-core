package com.zomu.t.epion.tropic.test.tool.core.flow.model;

import com.zomu.t.epion.tropic.test.tool.core.annotation.FlowDefinition;
import com.zomu.t.epion.tropic.test.tool.core.flow.runner.impl.BranchFlowRunner;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Flow;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

@Getter
@Setter
@FlowDefinition(id = "Branch", runner = BranchFlowRunner.class)
public class BranchFlow extends Flow {

    @NotEmpty
    private String condition;

    @NotEmpty
    private String trueRef;

    @NotEmpty
    private String falseRef;

}
