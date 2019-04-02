package com.epion_t3.core.command.reporter.impl;

import com.epion_t3.core.command.reporter.CommandReporter;
import com.epion_t3.core.context.execute.ExecuteCommand;
import com.epion_t3.core.context.execute.ExecuteContext;
import com.epion_t3.core.context.execute.ExecuteFlow;
import com.epion_t3.core.context.execute.ExecuteScenario;
import com.epion_t3.core.model.scenario.Command;

import java.util.Map;

/**
 * @param <COMMAND>
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @param <EXECUTE_FLOW>
 * @param <EXECUTE_COMMAND>
 * @author takashno
 */
public interface ThymeleafCommandReporter<COMMAND extends Command,
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        EXECUTE_FLOW extends ExecuteFlow,
        EXECUTE_COMMAND extends ExecuteCommand>
        extends CommandReporter<COMMAND, EXECUTE_CONTEXT, EXECUTE_SCENARIO, EXECUTE_FLOW, EXECUTE_COMMAND> {

    /**
     * @return
     */
    String templatePath();

    /**
     * @param variable
     */
    void setVariables(Map<String, Object> variable,
                      COMMAND command,
                      EXECUTE_CONTEXT executeContext,
                      EXECUTE_SCENARIO executeScenario,
                      EXECUTE_FLOW executeFlow,
                      EXECUTE_COMMAND executeCommand);

}

