/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.common.annotation.OriginalProcessField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * コマンド.
 */
@Getter
@Setter
public class Command implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @OriginalProcessField
    private String id;

    @OriginalProcessField
    private String summary;

    @OriginalProcessField
    private String description;

    @NotEmpty
    @OriginalProcessField
    private String command;

    @OriginalProcessField
    private String target;

    @OriginalProcessField
    private String value;

    @OriginalProcessField
    private ProcessReference ref;

    /**
     * 観点.<br>
     * Assert系コマンドの場合にテスト観点を設定する.<br>
     * 必須ではない.
     *
     * @since 0.0.4
     */
    @OriginalProcessField
    private List<String> viewPoint;

}
