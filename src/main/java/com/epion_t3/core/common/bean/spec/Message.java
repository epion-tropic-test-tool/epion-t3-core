package com.epion_t3.core.common.bean.spec;

import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * メッセージ定義.
 *
 * @author takashno
 */
@Getter
@Setter
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private String id;

    @NotEmpty
    @Valid
    private List<Content> message;
}
