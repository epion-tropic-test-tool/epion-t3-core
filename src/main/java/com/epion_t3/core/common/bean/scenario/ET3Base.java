package com.epion_t3.core.common.bean.scenario;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.epion_t3.core.command.resolver.CommandTypeIdResolver;
import com.epion_t3.core.configuration.resolver.CustomConfigurationTypeIdResolver;
import com.epion_t3.core.flow.resolver.FlowTypeIdResolver;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * シナリオモデル.
 */
@Getter
@Setter
public class ET3Base implements Serializable {

    /**
     * バージョン.
     * 今使えていない・・・
     */
    private String t3 = "1.0";

    /**
     * 情報.
     * 必須にしたいけど微妙
     */
    private Information info;

    /**
     * シナリオ.
     */
    @Valid
    private List<Scenario> scenarios = new ArrayList<>();

    /**
     * フロー.
     * コマンドおよび制御フローをどの順序で利用するかを定義するもの.
     */
    // MEMO:visible属性を「true」にしないとパースした際に値が設定されないらしい
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
    @JsonTypeIdResolver(FlowTypeIdResolver.class)
    @Valid
    private List<Flow> flows = new ArrayList<>();

    /**
     * コマンド.
     * 実行指示の最小単位.コマンドに何を行うかを細かく定義することになる.
     */
    // MEMO:visible属性を「true」にしないとパースした際に値が設定されないらしい
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "command", visible = true)
    @JsonTypeIdResolver(CommandTypeIdResolver.class)
    @Valid
    private List<Command> commands = new ArrayList<>();

    /**
     * 設定.
     */
    // MEMO:visible属性を「true」にしないとパースした際に値が設定されないらしい
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "configuration", visible = true)
    @JsonTypeIdResolver(CustomConfigurationTypeIdResolver.class)
    @Valid
    private List<Configuration> configurations = new ArrayList<>();

    /**
     * 変数.
     * 影響を及ぼせる範囲でスコープを切る.
     */
    private Variable variables;

    /**
     * プロファイル.
     * 実行する状況毎に分割する単位.
     */
    private Map<String, Map<String, String>> profiles;

    /**
     * カスタム機能定義.
     */
    private Custom customs;

}
