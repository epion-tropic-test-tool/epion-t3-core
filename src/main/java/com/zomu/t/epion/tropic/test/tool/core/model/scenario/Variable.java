package com.zomu.t.epion.tropic.test.tool.core.model.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class Variable implements Serializable {

    private Map<String, Object> global;

    private Map<String, Object> scenario;

    private Map<String, Object> local;

}
