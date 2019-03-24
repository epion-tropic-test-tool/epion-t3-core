package com.zomu.t.epion.tropic.test.tool.core.application.runner;

public interface ApplicationRunner<Context> {

    int execute(String[] args);

    void handleGlobalException(final Context context, final Throwable t);

}
