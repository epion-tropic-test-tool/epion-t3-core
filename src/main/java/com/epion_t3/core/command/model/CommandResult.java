package com.epion_t3.core.command.model;

import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.type.CommandStatus;
import lombok.Getter;
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



