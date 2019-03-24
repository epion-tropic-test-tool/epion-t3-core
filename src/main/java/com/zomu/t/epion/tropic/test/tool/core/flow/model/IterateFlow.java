package com.zomu.t.epion.tropic.test.tool.core.flow.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.zomu.t.epion.tropic.test.tool.core.flow.resolver.FlowTypeIdResolver;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Flow;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 繰り返し処理を行うためのFlow定義.
 */
@Getter
@Setter
public class IterateFlow extends Flow {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 子要素Flow.
     */
    // MEMO:visible属性を「true」にしないとパースした際に値が設定されないらしい
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
    @JsonTypeIdResolver(FlowTypeIdResolver.class)
    @Valid
    private List<Flow> children = new ArrayList<>();

}
