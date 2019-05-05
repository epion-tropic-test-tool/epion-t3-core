package com.epion_t3.core.command.resolver.impl;

import com.epion_t3.core.command.handler.CommandRunnerInvocationHandler;
import com.epion_t3.core.command.resolver.CommandRunnerResolver;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.CommandInfo;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.exception.CommandNotFoundException;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.impl.CoreMessages;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;

/**
 * @author takashno
 */
public final class CommandRunnerResolverImpl implements CommandRunnerResolver {

    /**
     * インスタンス.
     */
    private static final CommandRunnerResolverImpl instance = new CommandRunnerResolverImpl();

    /**
     * プライベートコンストラクタ.
     */
    private CommandRunnerResolverImpl() {
        // Do Nothing...
    }

    /**
     * インスタンス取得.
     *
     * @return
     */
    public static CommandRunnerResolverImpl getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * @param commandId
     * @return
     */
    @Override
    public CommandRunner getCommandRunner(
            String commandId,
            Context context,
            ExecuteContext executeContext,
            ExecuteScenario executeScenario,
            ExecuteFlow executeFlow,
            ExecuteCommand executeCommand) {

        if (StringUtils.isEmpty(commandId)) {
            // 不正
            throw new SystemException(CoreMessages.CORE_ERR_0001);
        }

        CommandInfo commandInfo = CustomPackageHolder.getInstance().getCustomCommandInfo(commandId);

        if (commandInfo == null) {
            // コマンド解決が出来ない場合
            throw new CommandNotFoundException(commandId);
        }

        // コマンド情報を保持
        executeCommand.setCommandInfo(commandInfo);

        // 実行クラスを取得
        Class runnerClass = commandInfo.getRunner();

        if (runnerClass == null) {
            // クラスが設定されていない場合（コンパイルが通らないレベルのため通常発生しない）
            throw new SystemException(CoreMessages.CORE_ERR_0001);
        }

        try {
            // インスタンス生成＋返却
            CommandRunner commandRunner = CommandRunner.class.cast(runnerClass.newInstance());
            CommandRunnerInvocationHandler commandRunnerInvocationHandler =
                    new CommandRunnerInvocationHandler(
                            commandRunner,
                            context,
                            executeContext,
                            executeScenario,
                            executeFlow,
                            executeCommand);

            // Proxyを作成
            CommandRunner commandRunnerProxy = (CommandRunner) Proxy.newProxyInstance(
                    CommandRunnerResolverImpl.class.getClassLoader(),
                    new Class<?>[]{CommandRunner.class},
                    commandRunnerInvocationHandler);

            // 返却
            return commandRunnerProxy;

        } catch (Exception e) {
            throw new SystemException(e, CoreMessages.CORE_ERR_0001);
        }
    }
}
