package com.zomu.t.epion.tropic.test.tool.core.flow.runner;

import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.FlowResult;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Flow;
import org.slf4j.Logger;

/**
 * フロー実行処理インタフェース.
 *
 * @param <FLOW>
 * @author takashno
 */
public interface FlowRunner<
        Context,
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        FLOW extends Flow> {

    /**
     * @param executeContext
     * @param executeScenario
     * @param flow
     */
    FlowResult execute(
            final Context executeContext,
            final EXECUTE_CONTEXT execute_context,
            final EXECUTE_SCENARIO executeScenario,
            final FLOW flow,
            final Logger logger);

}
