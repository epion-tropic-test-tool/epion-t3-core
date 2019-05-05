package com.epion_t3.core.common.context;

import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.type.ApplicationExecuteStatus;
import com.epion_t3.core.common.type.ExitCode;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.common.bean.ET3Notification;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 実行時の情報保持クラス.
 *
 * @author takashno
 */
@Getter
@Setter
public class ExecuteContext implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 実行ID
     */
    private UUID executeContextId = UUID.randomUUID();

    /**
     * ステージ.
     */
    private StageType stage = StageType.INITIALIZE;

    /**
     * ステータス.
     */
    private ApplicationExecuteStatus status = ApplicationExecuteStatus.WAIT;

    /**
     * 開始日時.
     */
    private LocalDateTime start = LocalDateTime.now();

    /**
     * 終了日時.
     */
    private LocalDateTime end;

    /**
     * 所要時間.
     */
    private Duration duration;

    /**
     * 実行したシナリオリスト.
     */
    private List<ExecuteScenario> scenarios = new ArrayList<>();

    /**
     * Globalスコープ変数.
     */
    private final Map<String, Object> globalVariables = new ConcurrentHashMap<>();

    /**
     * 実行結果ディレクトリパス.
     */
    private Path resultRootPath;

    /**
     * 終了コード.
     */
    private ExitCode exitCode = status.getExitCode();

    /**
     * 通知情報.
     */
    private List<ET3Notification> notifications = new ArrayList<>();

    /**
     * 通知を追加.
     *
     * @param notification 通知情報
     */
    public void addNotification(ET3Notification notification) {
        this.notifications.add(notification);
    }

    /**
     * エラー通知を含むかどうか.
     *
     * @return true: 含む, false: 含まない
     */
    public boolean hasErrorNotification() {
        return this.notifications.stream().anyMatch(x -> x.getLevel() == NotificationType.ERROR);
    }

}
