package com.epion_t3.core.context.execute;

import com.epion_t3.core.command.model.AssertCommandResult;
import com.epion_t3.core.command.model.CommandResult;
import com.epion_t3.core.context.CommandInfo;
import com.epion_t3.core.holder.CommandLog;
import com.epion_t3.core.model.scenario.Command;
import com.epion_t3.core.type.AssertStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ExecuteCommand extends ExecuteElement {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * コマンド識別子.
     */
    private String fqcn;

    /**
     * コマンド結果.
     */
    private CommandResult commandResult;

    /**
     * コマンド情報.
     * コマンドからコマンド情報を解決した際に保持しておく.
     */
    private CommandInfo commandInfo;

    /**
     * 対象コマンド.
     */
    private Command command;

    /**
     * 拡張プロセスフィールド.
     */
    private Map<String, Object> extensionProcessFields;

    /**
     * プロセスログリスト.
     */
    private List<CommandLog> commandLogs;

    /**
     * カスタムレポートパス.
     */
    private String customReportRelativePath;


    public boolean isAssertCommand() {
        return commandInfo.getAssertCommand();
    }

    public boolean hasAssertError() {
        if (isAssertCommand()) {
            if (commandResult != null && AssertCommandResult.class.isAssignableFrom(commandResult.getClass())) {
                return ((AssertCommandResult) commandResult).getAssertStatus() == AssertStatus.NG;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
