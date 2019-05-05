package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String id;

    @NotEmpty
    private List<Content> summary;

    private Boolean assertCommand = false;

    private Boolean evidenceCommand = false;

    @NotEmpty
    @Valid
    private List<TestItem> testItem = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<Function> function = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<Structure> structure = new ArrayList<>();

}
