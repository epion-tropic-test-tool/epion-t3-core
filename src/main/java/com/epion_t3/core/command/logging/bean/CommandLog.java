/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.logging.bean;

import ch.qos.logback.classic.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * プロセスログの１行毎の保持オブジェクト.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommandLog implements Serializable {

    @NonNull
    private String executeId;

    @NonNull
    private Level level;

    @NonNull
    private String message;

    @NonNull
    private LocalDateTime dateTime;

}
