/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.type.CommandStatus;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.logging.bean.FlowLog;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flow実行情報保持クラス.
 *
 * @author takashno
 */
@Getter
@Setter
public class ExecuteFlow extends ExecuteElement {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Flow.
     */
    private Flow flow;

    /**
     * Flow結果.
     */
    private FlowResult flowResult;

    /**
     * 実行コマンドリスト.
     */
    private final List<ExecuteCommand> commands = new ArrayList<>();

    /**
     * Flowスコープ変数.
     */
    private final Map<String, Object> flowVariables = new ConcurrentHashMap<>();

    /**
     * Flowログ.
     */
    private List<FlowLog> flowLogs;

    /**
     * コマンドに実行エラーが存在するか判定.
     * 
     * @return true : 存在する、false : 存在しない
     */
    public boolean hasCommandError() {
        return commands.stream().anyMatch(x -> x.getCommandResult().getStatus() == CommandStatus.ERROR);
    }

    /**
     * コマンドに実行正常かつ、アサートエラーが存在するか判定.
     * 
     * @return true : 存在する、false : 存在しない
     */
    public boolean hasCommandAssertError() {
        return commands.stream()
                .anyMatch(x -> x.getCommandResult().getStatus() == CommandStatus.SUCCESS && x.hasAssertError());
    }

}
