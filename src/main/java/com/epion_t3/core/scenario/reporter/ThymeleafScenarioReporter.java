package com.epion_t3.core.scenario.reporter;

import com.epion_t3.core.context.Context;
import com.epion_t3.core.context.execute.ExecuteContext;
import com.epion_t3.core.context.execute.ExecuteScenario;
import com.epion_t3.core.scenario.reporter.ScenarioReporter;

import java.util.Map;

/**
 * Thymeleafによるシナリオレポート出力インタフェース.
 *
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @author takashno
 */
public interface ThymeleafScenarioReporter<
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario>
        extends ScenarioReporter<EXECUTE_CONTEXT, EXECUTE_SCENARIO> {

    /**
     * @return
     */
    String templatePath();

    /**
     * @param variable
     */
    void setVariables(Map<String, Object> variable);

}
