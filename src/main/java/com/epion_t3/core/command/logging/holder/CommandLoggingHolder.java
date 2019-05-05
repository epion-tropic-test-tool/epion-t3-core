package com.epion_t3.core.command.logging.holder;

import com.epion_t3.core.command.logging.bean.CommandLog;

import java.util.ArrayList;

/**
 * プロセスログを保持するクラス.
 *
 * @author takashno
 */
public final class CommandLoggingHolder {

    /**
     * プロセスログを保持するスレッドローカル.
     */
    private static final ThreadLocal<ArrayList<CommandLog>> messages = new ThreadLocal<ArrayList<CommandLog>>() {
        @Override
        protected ArrayList<CommandLog> initialValue() {
            return new ArrayList<>();
        }
    };

    /**
     * ログ追加.
     *
     * @param log
     */
    public static void append(CommandLog log) {
        messages.get().add(log);
    }

    /**
     * ログクリア.
     */
    public static void clear() {
        messages.remove();
    }

    /**
     * 取得.
     *
     * @return
     */
    public static ArrayList<CommandLog> get() {
        return messages.get();
    }

}
