/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.common.annotation.OriginalCommandField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

/**
 * 設定.
 */
@Getter
@Setter
public class Configuration implements Serializable {

    /**
     * DefaultSerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Configuration.
     */
    @NotEmpty
    @OriginalCommandField
    private String configuration;

    @NotEmpty
    @OriginalCommandField
    private String id;

    @OriginalCommandField
    private String summary;

    @OriginalCommandField
    private String description;

}
