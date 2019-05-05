package com.epion_t3.core.command.handler;

import com.epion_t3.core.command.handler.listener.CommandListenerFactory;
import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * コマンド実行時のプロキシクラス.
 * このプロキシクラスでは、以下のハンドリングを行う.
 *
 * @author takashno
 */
public class CommandRunnerInvocationHandler<COMMAND_RUNNER extends CommandRunner> implements InvocationHandler {

    private final COMMAND_RUNNER commandRunner;

    private final Context context;

    private final ExecuteContext executeContext;

    private final ExecuteScenario executeScenario;

    private final ExecuteFlow executeFlow;

    private final ExecuteCommand executeCommand;
    

    /**
     * コンストラクタ.
     *
     * @param commandRunner
     */
    public CommandRunnerInvocationHandler(
            COMMAND_RUNNER commandRunner,
            Context context,
            ExecuteContext executeContext,
            ExecuteScenario executeScenario,
            ExecuteFlow executeFlow,
            ExecuteCommand executeCommand) {
        this.commandRunner = commandRunner;
        this.context = context;
        this.executeContext = executeContext;
        this.executeScenario = executeScenario;
        this.executeFlow = executeFlow;
        this.executeCommand = executeCommand;
    }

    /**
     * コマンド実行ハンドラ.
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 処理結果
        Object result = null;

        try {

            // コマンド前処理リスナーを実行
            CommandListenerFactory.getInstance().getBeforeListener().forEach(x -> x.beforeCommand(
                    commandRunner,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    executeCommand
            ));


            // Before Handle
            beforeHandle(proxy, method, args);

            // 実行
            result = method.invoke(commandRunner, args);

            // After Handle
            afterHandle(proxy, method, args);

            // コマンド後処理リスナーを実行
            CommandListenerFactory.getInstance().getAfterListener().forEach(x -> x.afterCommand(
                    commandRunner,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    executeCommand
            ));

        } catch (Throwable e) {

            // コマンド後処理リスナーを実行
            CommandListenerFactory.getInstance().getErrorListener().forEach(x -> x.afterCommand(
                    commandRunner,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    executeCommand,
                    e
            ));

            throw e;

        }

        return result;
    }

    /**
     * 処理前ハンドリング.
     *
     * @param proxy  対象オブジェクト
     * @param method メソッド
     * @param args   引数
     */
    private void beforeHandle(Object proxy, Method method, Object[] args) {
        // Do Nothing...
        // 実行対象のメソッド名を取得
        // String methodName = method.getName();
        // switch (methodName) {
        //     default:
        //         break;
        // }
    }

    /**
     * 処理後ハンドリング.
     *
     * @param proxy  対象オブジェクト
     * @param method メソッド
     * @param args   引数
     */
    private void afterHandle(Object proxy, Method method, Object[] args) {
        // Do Nothing...
        // 実行対象のメソッド名を取得
        // String methodName = method.getName();
        // switch (methodName) {
        //     default:
        //         break;
        // }
    }

}
