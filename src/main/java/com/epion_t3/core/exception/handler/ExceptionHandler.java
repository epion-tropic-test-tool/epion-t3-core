package com.epion_t3.core.exception.handler;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;

public interface ExceptionHandler<C extends Context, E extends ExecuteContext> {

    void handle(final C context,final E executeContext, final Throwable t);

}
