/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.logging.holder;

import com.epion_t3.core.command.logging.bean.CommandLog;
import lombok.NonNull;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * プロセスログを保持するクラス.
 *
 * @author takashno
 */
public final class CommandLoggingHolder {

    /**
     * プロセスログを保持するスレッドローカル.
     */
    private static final ThreadLocal<Map<String, ArrayList<CommandLog>>> messages = new ThreadLocal<Map<String, ArrayList<CommandLog>>>() {
        @Override
        protected Map<String, ArrayList<CommandLog>> initialValue() {
            return new HashMap<>();
        }
    };

    /**
     * ログ追加.
     *
     * @param log
     */
    public static void append(CommandLog log) {
        if (!messages.get().containsKey(log.getExecuteId())) {
            messages.get().put(log.getExecuteId(), new ArrayList<>());
        }
        messages.get().get(log.getExecuteId()).add(log);
    }

    /**
     * ログクリア.
     */
    public static void clear(@NonNull String executeId) {
        messages.get().remove(executeId);
    }

    /**
     * 取得.
     *
     * @return Commandログリスト
     */
    public static ArrayList<CommandLog> get(@NonNull String executeId) {
        if (!messages.get().containsKey(executeId)) {
            return new ArrayList<>(0);
        } else {
            return SerializationUtils.clone(messages.get().get(executeId));
        }
    }

}
