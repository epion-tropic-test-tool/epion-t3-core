package com.epion_t3.core.common.bean;

import com.epion_t3.core.command.bean.AssertCommandResult;
import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.logging.bean.CommandLog;
import com.epion_t3.core.common.type.AssertStatus;
import com.epion_t3.core.common.bean.scenario.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * コマンド実行情報.
 *
 * @author takashno
 */
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


    /**
     * アサート系コマンドか判定する.
     *
     * @return <code>true</code>: アサート系コマンド, <code>false</code>: アサート系コマンド以外
     */
    public boolean isAssertCommand() {
        return commandInfo.getAssertCommand();
    }

    /**
     * アサートエラーが含まれるか判定する.
     *
     * @return <code>true</code>: アサートエラーが含まれる, <code>false</code>: アサートエラーが含まれない
     */
    public boolean hasAssertError() {
        if (isAssertCommand()) {
            if (commandResult != null
                    && AssertCommandResult.class.isAssignableFrom(commandResult.getClass())) {
                return ((AssertCommandResult) commandResult).getAssertStatus()
                        == AssertStatus.NG;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
