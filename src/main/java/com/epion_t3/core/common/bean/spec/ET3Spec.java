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
public class ET3Spec implements Serializable {

    /**
     * バージョン.
     */
    private String et3 = "1.0";

    /**
     * カスタム機能情報.
     */
    @NotNull
    private Information info;

    /**
     * コマンド.
     */
    @NotEmpty
    @Valid
    private List<Command> commands;

    /**
     * メッセージ定義.
     */
    @NotEmpty
    @Valid
    private List<Message> messages;


}
