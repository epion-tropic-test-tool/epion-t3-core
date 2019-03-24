package com.zomu.t.epion.tropic.test.tool.core.holder;

import ch.qos.logback.classic.Level;
import lombok.*;

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
    private Level level;

    @NonNull
    private String message;

    @NonNull
    private LocalDateTime dateTime;


}
