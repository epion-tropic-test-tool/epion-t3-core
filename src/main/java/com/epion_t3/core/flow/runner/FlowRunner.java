package com.epion_t3.core.flow.runner;

import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.common.bean.scenario.Flow;
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
