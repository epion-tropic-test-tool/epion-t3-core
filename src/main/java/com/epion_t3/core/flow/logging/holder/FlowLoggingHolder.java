package com.epion_t3.core.flow.logging.holder;

import com.epion_t3.core.flow.logging.bean.FlowLog;

import java.util.ArrayList;

/**
 * Flowログを保持するクラス.
 *
 * @author takashno
 */
public final class FlowLoggingHolder {

    /**
     * プロセスログを保持するスレッドローカル.
     */
    private static final ThreadLocal<ArrayList<FlowLog>> messages = new ThreadLocal<ArrayList<FlowLog>>() {
        @Override
        protected ArrayList<FlowLog> initialValue() {
            return new ArrayList<>();
        }
    };

    /**
     * ログ追加.
     *
     * @param log
     */
    public static void append(FlowLog log) {
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
    public static ArrayList<FlowLog> get() {
        return messages.get();
    }

}
