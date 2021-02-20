/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * カスタム設定.
 *
 * @author takashno
 */
@Getter
@Setter
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String id;

    @NotEmpty
    private List<Content> summary;

    @NotEmpty
    @Valid
    private List<Description> description = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<Structure> structure = new ArrayList<>();

}
