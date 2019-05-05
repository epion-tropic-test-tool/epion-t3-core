package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TestItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer order;

    @NotEmpty
    @Valid
    private List<Content> summary;

}
