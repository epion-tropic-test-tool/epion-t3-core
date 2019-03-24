package com.zomu.t.epion.tropic.test.tool.core.command.model;

import com.zomu.t.epion.tropic.test.tool.core.flow.model.FlowResult;
import com.zomu.t.epion.tropic.test.tool.core.type.CommandStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

/**
 * コマンド結果.
 *
 * @author takashno
 */
@Getter
@Setter
public class CommandResult implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ステータス.
     */
    private CommandStatus status = CommandStatus.WAIT;

    /**
     * メッセージ.
     */
    private String message;

    /**
     * デフォルト状態のFlow結果オブジェクトを取得する.
     *
     * @return {@link FlowResult}
     */
    public static CommandResult getDefault() {
        return new CommandResult();
    }

    public static CommandResult getSuccess() {
        CommandResult commandResult = new CommandResult();
        commandResult.setStatus(CommandStatus.SUCCESS);
        return commandResult;
    }

}



