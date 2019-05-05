package com.epion_t3.core.custom.parser;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;

/**
 * カスタム機能解析インタフェース.
 *
 * @author takashno
 */
public interface CustomParser<C extends Context, E extends ExecuteContext> {

    /**
     * @param context
     */
    void parse(final C context, final E executeContext);


}
