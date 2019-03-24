package com.zomu.t.epion.tropic.test.tool.core.logging.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.zomu.t.epion.tropic.test.tool.core.holder.FlowLog;
import com.zomu.t.epion.tropic.test.tool.core.holder.FlowLoggingHolder;

import java.time.LocalDateTime;

/**
 * Flowのログを収集するためのアペンダー.
 */
public class FlowLoggingAppender extends AppenderBase<ILoggingEvent> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(ILoggingEvent eventObject) {
        Level level = eventObject.getLevel();
        String message = eventObject.getFormattedMessage();
        LocalDateTime now = LocalDateTime.now();
        FlowLoggingHolder.append(FlowLog.builder()
                .level(level)
                .message(message)
                .dateTime(now).build());
    }

}
