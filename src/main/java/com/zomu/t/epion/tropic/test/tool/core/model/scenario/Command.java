package com.zomu.t.epion.tropic.test.tool.core.model.scenario;

import com.zomu.t.epion.tropic.test.tool.core.annotation.OriginalProcessField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

@Getter
@Setter
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @OriginalProcessField
    private String id;

    @OriginalProcessField
    private String summary;

    @OriginalProcessField
    private String description;

    @NotEmpty
    @OriginalProcessField
    private String command;

    @OriginalProcessField
    private String target;

    @OriginalProcessField
    private String value;

    @OriginalProcessField
    private ProcessReference ref;

}
