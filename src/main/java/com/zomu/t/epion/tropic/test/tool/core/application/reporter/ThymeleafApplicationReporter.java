package com.zomu.t.epion.tropic.test.tool.core.application.reporter;

import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.scenario.reporter.ScenarioReporter;

import java.util.Map;

/**
 * Thymeleafによるシナリオレポート出力インタフェース.
 *
 * @param <EXECUTE_CONTEXT>
 * @author takashno
 */
public interface ThymeleafApplicationReporter<
        EXECUTE_CONTEXT extends ExecuteContext>
        extends ApplicationReporter<EXECUTE_CONTEXT> {

    /**
     * @return
     */
    String templatePath();

    /**
     * @param variable
     */
    void setVariables(Map<String, Object> variable);

}
