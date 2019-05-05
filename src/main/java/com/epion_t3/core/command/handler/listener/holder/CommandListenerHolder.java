package com.epion_t3.core.command.handler.listener.holder;


import com.epion_t3.core.command.handler.listener.CommandAfterListener;
import com.epion_t3.core.command.handler.listener.CommandBeforeListener;
import com.epion_t3.core.command.handler.listener.CommandErrorListener;

import java.util.LinkedList;
import java.util.List;

/**
 * コマンドリスナー保持クラス.
 *
 * @author takashno
 */
public final class CommandListenerHolder {

    /**
     * シングルトンインスタンス.
     */
    private static final CommandListenerHolder instance = new CommandListenerHolder();

    /**
     * コマンド前処理リスナークラスリスト.
     */
    private static final List<Class<? extends CommandBeforeListener>> commandBeforeListenerClasses = new LinkedList<>();

    /**
     * コマンド前処理リスナークラスリスト.
     */
    private static final List<Class<? extends CommandAfterListener>> commandAfterListenerClasses = new LinkedList<>();

    /**
     * コマンド前処理リスナークラスリスト.
     */
    private static final List<Class<? extends CommandErrorListener>> commandErrorListenerClasses = new LinkedList<>();

    /**
     * プライベートコンストラクタ.
     */
    private CommandListenerHolder() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得します.
     *
     * @return
     */
    public static CommandListenerHolder getInstance() {
        return instance;
    }

    /**
     * コマンド前処理リスナークラスを追加.
     *
     * @param clazz
     */
    public void addCommandBeforeListener(Class<? extends CommandBeforeListener> clazz) {
        commandBeforeListenerClasses.add(clazz);
    }

    /**
     * コマンド
     *
     * @return
     */
    public List<Class<? extends CommandBeforeListener>> getCommandBeforeListenerClasses() {
        return commandBeforeListenerClasses;
    }

    /**
     * コマンド後処理リスナークラスを追加.
     *
     * @param clazz
     */
    public void addCommandAfterListener(Class<? extends CommandAfterListener> clazz) {
        commandAfterListenerClasses.add(clazz);
    }

    /**
     * コマンド後処理リスナークラスリストを取得します.
     *
     * @return
     */
    public List<Class<? extends CommandAfterListener>> getCommandAfterListenerClasses() {
        return commandAfterListenerClasses;
    }

    /**
     * コマンドエラー処理リスナーを追加.
     *
     * @param clazz
     */
    public void addCommandErrorListener(Class<? extends CommandErrorListener> clazz) {
        commandErrorListenerClasses.add(clazz);
    }

    /**
     * @return
     */
    public List<Class<? extends CommandErrorListener>> getCommandErrorListenerClasses() {
        return commandErrorListenerClasses;
    }
}
