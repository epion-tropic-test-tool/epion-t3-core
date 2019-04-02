package com.epion_t3.core.context;

import com.epion_t3.core.flow.runner.FlowRunner;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class FlowInfo implements Serializable {

    @NonNull
    private String id;

    private String summary;

    private String description;

    @NonNull
    private Class<?> model;

    @NonNull
    private Class<? extends FlowRunner> runner;

}
