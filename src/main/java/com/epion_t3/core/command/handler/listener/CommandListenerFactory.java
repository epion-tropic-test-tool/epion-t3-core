/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.handler.listener;

import com.epion_t3.core.command.handler.listener.holder.CommandListenerHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * コマンドリスナー生成クラス.
 *
 * @author takashno
 */
public final class CommandListenerFactory {

    /**
     * シングルトンインスタンス.
     */
    private static final CommandListenerFactory instance = new CommandListenerFactory();

    /**
     * プライベートコンストラクタ.
     */
    private CommandListenerFactory() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得します.
     *
     * @return
     */
    public static CommandListenerFactory getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    public List<CommandBeforeListener> getBeforeListener() {
        final var result = new LinkedList<CommandBeforeListener>();
        CommandListenerHolder.getInstance().getCommandBeforeListenerClasses().forEach(x -> {
            try {
                result.add(x.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    /**
     *
     * @return
     */
    public List<CommandAfterListener> getAfterListener() {
        final var result = new LinkedList<CommandAfterListener>();
        CommandListenerHolder.getInstance().getCommandAfterListenerClasses().forEach(x -> {
            try {
                result.add(x.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    /**
     *
     * @return
     */
    public List<CommandErrorListener> getErrorListener() {
        final var result = new LinkedList<CommandErrorListener>();
        CommandListenerHolder.getInstance().getCommandErrorListenerClasses().forEach(x -> {
            try {
                result.add(x.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

}
