/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 実行情報保持のための基底クラス.
 */
@Getter
@Setter
public abstract class ExecuteElement implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 実行時ID
     */
    private UUID executeId = UUID.randomUUID();

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
     * スタックトレース（エラー時のみ）.
     */
    private String stackTrace;

}
