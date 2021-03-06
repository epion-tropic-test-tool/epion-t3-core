/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.flow.resolver.FlowTypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 子Flowを持つFlow定義.
 */
@Getter
@Setter
public class HasChildrenFlow extends Flow {

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
