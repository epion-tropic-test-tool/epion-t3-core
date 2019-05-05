package com.epion_t3.core.common.bean;

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

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    @NonNull
    private String id;

    private String summary;

    private String description;

    @NonNull
    private Class<?> model;

    @NonNull
    private Class<? extends FlowRunner> runner;

}
