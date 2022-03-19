/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.bean;

import com.epion_t3.core.common.type.CommandStatus;
import com.epion_t3.core.flow.bean.FlowResult;
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

    /**
     * 成功時のコマンド結果を取得する.
     *
     * @return コマンド結果
     */
    public static CommandResult getSuccess() {
        var commandResult = new CommandResult();
        commandResult.setStatus(CommandStatus.SUCCESS);
        return commandResult;
    }

}
