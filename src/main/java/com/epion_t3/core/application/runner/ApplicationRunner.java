/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.application.runner;

public interface ApplicationRunner<Context> {

    int execute(String[] args);

}
