package com.epion_t3.core.scenario.runner;

import com.epion_t3.core.context.execute.ExecuteContext;

/**
 *
 * @param <EXECUTE_CONTEXT>
 */
public interface ScenarioRunner<Context, EXECUTE_CONTEXT extends ExecuteContext> {


    /**
     * @param context
     */
    void execute(final Context context, final EXECUTE_CONTEXT executeContext);

}
