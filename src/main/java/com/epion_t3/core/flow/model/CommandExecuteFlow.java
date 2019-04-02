package com.epion_t3.core.flow.model;

import com.epion_t3.core.annotation.FlowDefinition;
import com.epion_t3.core.flow.runner.impl.CommandExecuteFlowRunner;
import com.epion_t3.core.model.scenario.Flow;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

/**
 * コマンド実行を行うためのFlow定義.
 *
 * @author takashno
 */
@Getter
@Setter
@FlowDefinition(
        id = "CommandExecute", runner = CommandExecuteFlowRunner.class)
public class CommandExecuteFlow extends Flow {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 実行対象コマンドの参照ID.
     */
    @NotEmpty
    private String ref;

}
