package com.epion_t3.core.application.runner;

public interface ApplicationRunner<Context> {

    int execute(String[] args);

    void handleGlobalException(final Context context, final Throwable t);

}
