package com.epion_t3.core.application.reporter;

import com.epion_t3.core.common.context.ExecuteContext;

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
