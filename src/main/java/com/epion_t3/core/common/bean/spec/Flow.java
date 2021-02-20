/* Copyright (c) 2017-2020 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Flow設計情報.
 *
 * @author takashno
 */
@Getter
@Setter
public class Flow implements Serializable {

    /** デフォルトシリアルバージョンUID */
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String id;

    @NotEmpty
    private List<Content> summary;

    @NotEmpty
    @Valid
    private List<TestItem> testItem = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<Function> function = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<Structure> structure = new ArrayList<>();

}
