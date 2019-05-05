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
public class Structure implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer order;

    @NotEmpty
    private String name;

    @NotNull
    private Boolean required;

    @NotEmpty
    private String type;

    private String pattern;

    /**
     * 概要説明.
     */
    @NotEmpty
    @Valid
    private List<Content> summary;

    /**
     * 詳細説明.
     */
    private List<Content> description;

    /**
     * プロパティ.
     * typeがobjectの場合に、子階層を表す.
     * typeがarrayの場合に、要素型を表す.
     */
    @Valid
    private List<Structure> property;


}
