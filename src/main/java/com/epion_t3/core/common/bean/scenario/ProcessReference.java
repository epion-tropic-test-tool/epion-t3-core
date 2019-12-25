/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class ProcessReference implements Serializable {

    private String id;

    private Map<String, Object> args;

    private Map<String, Object> results;

}
