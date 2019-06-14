package com.epion_t3.core.flow.runner;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.context.Context;
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
        FLOW extends Flow,
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        EXECUTE_FLOW extends ExecuteFlow> {

    /**
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param flow
     * @param logger
     */
    FlowResult execute(
            final Context context,
            final EXECUTE_CONTEXT executeContext,
            final EXECUTE_SCENARIO executeScenario,
            final EXECUTE_FLOW executeFlow,
            final FLOW flow,
            final Logger logger);

}
