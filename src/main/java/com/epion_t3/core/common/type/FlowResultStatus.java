/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlowResultStatus {

    NEXT,

    CHOICE,

    EXIT;

}
