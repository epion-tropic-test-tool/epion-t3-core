package com.zomu.t.epion.tropic.test.tool.core.command.resolver;

import com.zomu.t.epion.tropic.test.tool.core.command.runner.CommandRunner;
import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteCommand;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;

/**
 * コマンド実行クラスの解決処理インタフェース.
 *
 * @author takashno
 */
public interface CommandRunnerResolver {

    /**
     * コマンド実行処理を取得する.
     * コマンド実行処理はProxyによって任意の拡張リスナー処理を実装できるようにする.
     * そのため、InvocationHandlerにてラップする形で提供を行うこと.
     *
     * @param commandId コマンドID
     * @return コマンド実行処理
     */
    CommandRunner getCommandRunner(
            String commandId,
            Context context,
            ExecuteContext executeContext,
            ExecuteScenario executeScenario,
            ExecuteFlow executeFlow,
            ExecuteCommand executeCommand);

}
