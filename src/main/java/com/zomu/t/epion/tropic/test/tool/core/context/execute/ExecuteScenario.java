package com.zomu.t.epion.tropic.test.tool.core.context.execute;

import com.zomu.t.epion.tropic.test.tool.core.context.EvidenceInfo;
import com.zomu.t.epion.tropic.test.tool.core.context.Option;
import com.zomu.t.epion.tropic.test.tool.core.type.ScenarioExecuteStatus;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Information;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * シナリオ実行時の情報保持クラス.
 *
 * @author takashno
 */
@Getter
@Setter
public class ExecuteScenario implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * シナリオ中のFlowの開始時間を参照するキーの接尾辞.
     */
    public static final String FLOW_START_VARIABLE_SUFFIX = "_START_TIME";

    /**
     * シナリオ中のFlowの終了時間を参照するキーの接尾辞.
     */
    public static final String FLOW_END_VARIABLE_SUFFIX = "_END_TIME";

    /**
     * 実行フローID
     */
    private UUID executeScenarioId = UUID.randomUUID();

    /**
     * オプション.
     */
    private Option option;

    /**
     * シナリオ情報.
     */
    private Information info;

    /**
     * 完全シナリオ名称.
     * Full Query Scenario Name.
     */
    private String fqsn;

    /**
     * ステータス.
     */
    private ScenarioExecuteStatus status = ScenarioExecuteStatus.WAIT;

    /**
     * 開始日時.
     */
    private LocalDateTime start;

    /**
     * 終了日時.
     */
    private LocalDateTime end;

    /**
     * 所要時間.
     */
    private Duration duration;

    /**
     * エラー.
     */
    private Throwable error;

    /**
     * 実行Flowリスト.
     */
    private List<ExecuteFlow> flows = new ArrayList<>();
    
    /**
     * プロファイル定数.
     */
    private final Map<String, String> profileConstants = new ConcurrentHashMap<>();

    /**
     * シナリオスコープ変数.
     */
    Map<String, Object> scenarioVariables = new ConcurrentHashMap<>();

    /**
     * エビデンスマップ.
     * 順序保証を持たせる.
     */
    LinkedHashMap<String, EvidenceInfo> evidences = new LinkedHashMap<>();

    /**
     * フローIDとエビデンスIDの変換マップ.
     * 順序保証を持たせる.
     * FlowIDをベースでエビデンスを逆引きする場合、繰り返しFlowに対応するにはリストで保持するほかない.
     * エビデンス参照を行う場合には、直近のFlowIDのエビデンスを参照することになる.
     */
    Map<String, LinkedList<String>> flowId2EvidenceId = new ConcurrentHashMap<>();

    /**
     * 実行結果ディレクトリパス.
     */
    private Path resultPath;

    /**
     * 実行結果-エビデンス格納パス.
     */
    private Path evidencePath;


}
