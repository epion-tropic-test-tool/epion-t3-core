/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.logging.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.epion_t3.core.flow.logging.bean.FlowLog;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Flowのログを収集するためのアペンダー.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowLoggingAppender extends AppenderBase<ILoggingEvent> {

    /**
     * シングルトンインスタンス.
     */
    private static final FlowLoggingAppender instance = new FlowLoggingAppender();

    /**
     * インスタンスを取得します.
     *
     * @return シングルトンインスタンス
     */
    public static FlowLoggingAppender getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(ILoggingEvent eventObject) {
        var flowExecuteId = eventObject.getMarker().getName();
        var level = eventObject.getLevel();
        var message = eventObject.getFormattedMessage();
        var now = LocalDateTime.now();
        FlowLoggingHolder
                .append(FlowLog.builder().executeId(flowExecuteId).level(level).message(message).dateTime(now).build());
    }

}
