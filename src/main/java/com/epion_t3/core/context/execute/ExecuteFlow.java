package com.epion_t3.core.context.execute;

import com.epion_t3.core.holder.FlowLog;
import com.epion_t3.core.model.scenario.Flow;
import com.epion_t3.core.type.CommandStatus;
import com.epion_t3.core.type.FlowStatus;
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
     * ステータス.
     */
    private FlowStatus status = FlowStatus.WAIT;

    /**
     * 実行Flowリスト.
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


    public boolean hasCommandError() {
        return commands.stream().anyMatch(x -> x.getCommandResult().getStatus() == CommandStatus.ERROR);
    }

}
