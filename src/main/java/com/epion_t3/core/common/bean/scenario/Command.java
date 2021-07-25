/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.common.annotation.OriginalCommandField;
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
    @OriginalCommandField
    private String id;

    @OriginalCommandField
    private String summary;

    @OriginalCommandField
    private String description;

    @NotEmpty
    @OriginalCommandField
    private String command;

    @OriginalCommandField
    private String target;

    @OriginalCommandField
    private String value;

    @OriginalCommandField
    private ProcessReference ref;

    /**
     * 観点.<br>
     * Assert系コマンドの場合にテスト観点を設定する.<br>
     * 必須ではない.
     *
     * @since 0.0.4
     */
    @OriginalCommandField
    private List<String> viewPoint;

}
