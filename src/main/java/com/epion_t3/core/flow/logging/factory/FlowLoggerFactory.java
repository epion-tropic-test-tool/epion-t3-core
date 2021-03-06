/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.logging.factory;

import com.epion_t3.core.flow.logging.appender.FlowLoggingAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FlowLoggerFactory {

    private static final FlowLoggerFactory instance = new FlowLoggerFactory();

    public static FlowLoggerFactory getInstance() {
        return instance;
    }

    private FlowLoggingAppender flowLoggingAppender;

    public Logger getFlowLogger(Class clazz) {
        var templateLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("FlowLog");
        var loggerContext = templateLogger.getLoggerContext();
        if (flowLoggingAppender == null) {
            flowLoggingAppender = new FlowLoggingAppender();
        }
        templateLogger.addAppender(flowLoggingAppender);
        if (!flowLoggingAppender.isStarted()) {
            flowLoggingAppender.start();
        }
        var logger = loggerContext.getLogger("FlowLog");
        logger.addAppender(flowLoggingAppender);
        return logger;
    }

}
