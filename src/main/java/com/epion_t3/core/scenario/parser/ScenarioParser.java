package com.epion_t3.core.scenario.parser;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;

/**
 * @author takashno
 */
public interface ScenarioParser<C extends Context, E extends ExecuteContext> {

    /**
     * @param context コンテキスト
     */
    void parse(final C context, final E executeContext);

}
