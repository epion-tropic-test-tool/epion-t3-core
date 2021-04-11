/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 詳細説明.<br>
 * 詳細説明はStructureのSummary説明を補足する目的で付与する.
 */
@Getter
@Setter
public class Description implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer order;

    private List<Content> summary;

}
