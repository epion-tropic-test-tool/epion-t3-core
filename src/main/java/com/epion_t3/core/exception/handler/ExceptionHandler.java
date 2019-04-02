package com.epion_t3.core.exception.handler;

public interface ExceptionHandler<Context> {

    void handle(final Context context, final Throwable t);

}
