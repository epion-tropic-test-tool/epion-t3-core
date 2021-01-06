/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlowResultStatus {

    NEXT,

    CHOICE,

    EXIT,

    /**
     * Iterate、While、DoWhileのループの次要素遷移. このステータスは、ループ系Flowの子Flowでのみ有効となる.
     * 
     * @since 0.0.3
     */
    CONTINUE,

    /**
     * Iterate、While、DoWhileのループの中断. このステータスは、ループ系Flowの子Flowでのみ有効となる.
     * 
     * @since 0.0.3
     */
    BREAK;

}
