package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

/**
 * コンテンツ共通.
 *
 * @author takashno
 */
@Getter
@Setter
public class Content implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String lang;

    @NotEmpty
    private String contents;

}
