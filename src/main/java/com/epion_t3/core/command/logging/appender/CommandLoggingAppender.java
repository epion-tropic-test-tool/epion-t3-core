/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.logging.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.epion_t3.core.command.logging.bean.CommandLog;
import com.epion_t3.core.command.logging.holder.CommandLoggingHolder;

import java.time.LocalDateTime;

/**
 * コマンドのログを収集するためのアペンダー.
 */
public class CommandLoggingAppender extends AppenderBase<ILoggingEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(ILoggingEvent eventObject) {
        var commandExecuteId = eventObject.getMarker() != null ? eventObject.getMarker().getName() : "CommandLog";
        var level = eventObject.getLevel();
        var message = eventObject.getFormattedMessage();
        var now = LocalDateTime.now();
        CommandLoggingHolder.append(
                CommandLog.builder().executeId(commandExecuteId).level(level).message(message).dateTime(now).build());
    }

}
