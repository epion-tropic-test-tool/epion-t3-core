package com.epion_t3.core.command.logging.appender;

import ch.qos.logback.classic.Level;
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
        Level level = eventObject.getLevel();
        String message = eventObject.getFormattedMessage();
        LocalDateTime now = LocalDateTime.now();
        CommandLoggingHolder.append(CommandLog.builder()
                .level(level)
                .message(message)
                .dateTime(now).build());
    }

}
