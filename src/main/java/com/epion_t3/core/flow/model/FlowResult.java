package com.epion_t3.core.flow.model;

import com.epion_t3.core.common.type.FlowResultStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Flow結果クラス.
 *
 * @author takashno
 */
@Getter
@Setter
public class FlowResult implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Flowの結果ステータス.
     */
    private FlowResultStatus status = FlowResultStatus.NEXT;

    /**
     * Flowとして次に実行するID.
     * 結果ステータスが「CHOICE」の時のみ利用する.
     */
    private String choiceId;

    /**
     * デフォルト状態のFlow結果オブジェクトを取得する.
     *
     * @return {@link FlowResult}
     */
    public static FlowResult getDefault() {
        return new FlowResult();
    }

}
