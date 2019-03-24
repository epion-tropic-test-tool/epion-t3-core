package com.zomu.t.epion.tropic.test.tool.core.command.reporter;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteCommand;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Command;

/**
 * コマンドレポート出力処理インタフェース.
 *
 * @param <COMMAND>
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @param <EXECUTE_FLOW>
 * @param <EXECUTE_COMMAND>
 * @author takashno
 */
public interface CommandReporter<
        COMMAND extends Command,
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        EXECUTE_FLOW extends ExecuteFlow,
        EXECUTE_COMMAND extends ExecuteCommand> {


    void report(COMMAND command,
                Context context,
                ExecuteContext executeContext,
                ExecuteScenario executeScenario,
                ExecuteFlow executeFlow,
                ExecuteCommand executeCommand,
                Throwable t);


}
