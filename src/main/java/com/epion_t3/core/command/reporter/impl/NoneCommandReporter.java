package com.epion_t3.core.command.reporter.impl;

import com.epion_t3.core.command.model.NoneCommand;
import com.epion_t3.core.context.execute.ExecuteCommand;
import com.epion_t3.core.context.execute.ExecuteContext;
import com.epion_t3.core.context.execute.ExecuteFlow;
import com.epion_t3.core.context.execute.ExecuteScenario;

import java.util.Map;

public class NoneCommandReporter
        extends AbstractThymeleafCommandReporter<NoneCommand> {
    @Override
    public String templatePath() {
        return null;
    }

    @Override
    public void setVariables(Map<String, Object> variable,
                             NoneCommand command,
                             ExecuteContext executeContext,
                             ExecuteScenario executeScenario,
                             ExecuteFlow executeFlow,
                             ExecuteCommand executeCommand) {
    }
}
