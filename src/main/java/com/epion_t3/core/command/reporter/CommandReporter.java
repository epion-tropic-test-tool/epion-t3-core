/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.reporter;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;

/**
 * コマンドレポート出力処理インタフェース.
 *
 * @param <COMMAND>
 * @param <COMMAND_RESULT>
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @param <EXECUTE_FLOW>
 * @param <EXECUTE_COMMAND>
 * @author takashno
 */
public interface CommandReporter<COMMAND extends Command, COMMAND_RESULT extends CommandResult, EXECUTE_CONTEXT extends ExecuteContext, EXECUTE_SCENARIO extends ExecuteScenario, EXECUTE_FLOW extends ExecuteFlow, EXECUTE_COMMAND extends ExecuteCommand> {

    /**
     * レポート出力を行う.
     * 
     * @param command
     * @param result
     * @param context
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param executeCommand
     * @param t
     */
    void report(COMMAND command, COMMAND_RESULT result, Context context, ExecuteContext executeContext,
            ExecuteScenario executeScenario, ExecuteFlow executeFlow, ExecuteCommand executeCommand, Throwable t);

}
