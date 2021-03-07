/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.logging.factory;

import com.epion_t3.core.command.logging.appender.CommandLoggingAppender;
import com.epion_t3.core.flow.logging.appender.FlowLoggingAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandLoggerFactory {

    private static final CommandLoggerFactory instance = new CommandLoggerFactory();

    public static CommandLoggerFactory getInstance() {
        return instance;
    }

    private CommandLoggingAppender commandLoggingAppender;

    public Logger getCommandLogger(Class clazz) {
        var templateLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("CommandLog");
        var loggerContext = templateLogger.getLoggerContext();
        if (commandLoggingAppender == null) {
            commandLoggingAppender = new CommandLoggingAppender();
        }
        templateLogger.addAppender(commandLoggingAppender);
        if (!commandLoggingAppender.isStarted()) {
            commandLoggingAppender.start();
        }
        var logger = loggerContext.getLogger("CommandLog");
        logger.addAppender(commandLoggingAppender);
        return logger;
    }

}
