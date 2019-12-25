/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MessageSpecInfo implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private String content;

}
