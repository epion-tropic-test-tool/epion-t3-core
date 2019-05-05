package com.epion_t3.core.common.bean.scenario;

import com.epion_t3.core.common.annotation.OriginalProcessField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

/**
 *
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
    @OriginalProcessField
    private String configuration;

    @NotEmpty
    @OriginalProcessField
    private String id;

    @OriginalProcessField
    private String summary;

    @OriginalProcessField
    private String description;

}
