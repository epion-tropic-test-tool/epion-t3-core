/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 試験項目書の説明.
 */
@Getter
@Setter
public class TestItem implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 順序.
     */
    @NotNull
    private Integer order;

    /**
     * 概要説明.
     */
    @NotEmpty
    @Valid
    private List<Content> summary;

}
