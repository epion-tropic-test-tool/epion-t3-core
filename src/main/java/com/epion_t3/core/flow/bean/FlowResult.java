/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.bean;

import com.epion_t3.core.common.type.FlowStatus;
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
    private FlowStatus status = FlowStatus.WAIT;

    /**
     * デフォルト状態のFlow結果オブジェクトを取得する.
     *
     * @return {@link FlowResult}
     */
    public static FlowResult getDefault() {
        return new FlowResult();
    }

    /**
     * 成功時のFlow結果を取得する.
     *
     * @return コマンド結果
     */
    public static FlowResult getSuccess() {
        var flowResult = new FlowResult();
        flowResult.setStatus(FlowStatus.SUCCESS);
        return flowResult;
    }

}
