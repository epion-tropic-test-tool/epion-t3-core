/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Description implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer order;

    private List<Content> summary;

}
