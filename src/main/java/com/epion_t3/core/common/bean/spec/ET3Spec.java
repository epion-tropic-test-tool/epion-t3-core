/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ET3Spec implements Serializable {

    /**
     * バージョン.
     */
    private String et3 = "1.0";

    /**
     * 対応言語.
     */
    private List<String> languages;

    /**
     * カスタム機能情報.
     */
    @NotNull
    private Information info;

    /**
     * Flow.
     */
    @Valid
    private List<Flow> flows;

    /**
     * コマンド.
     */
    @NotEmpty
    @Valid
    private List<Command> commands;

    /**
     * 設定情報.
     */
    @Valid
    private List<Configuration> configurations;

    /**
     * メッセージ定義.
     */
    @NotEmpty
    @Valid
    private List<Message> messages;

}
