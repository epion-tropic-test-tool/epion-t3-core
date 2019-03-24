package com.zomu.t.epion.tropic.test.tool.core.exception.handler;

public interface ExceptionHandler<Context> {

    void handle(final Context context, final Throwable t);

}
