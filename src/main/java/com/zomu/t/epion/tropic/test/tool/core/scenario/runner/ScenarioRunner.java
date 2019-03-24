package com.zomu.t.epion.tropic.test.tool.core.scenario.runner;

import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;

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
