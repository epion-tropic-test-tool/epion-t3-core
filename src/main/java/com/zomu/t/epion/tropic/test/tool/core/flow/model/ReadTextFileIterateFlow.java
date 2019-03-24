package com.zomu.t.epion.tropic.test.tool.core.flow.model;


import com.zomu.t.epion.tropic.test.tool.core.annotation.FlowDefinition;
import com.zomu.t.epion.tropic.test.tool.core.flow.runner.impl.ReadFileIterateFlowRunner;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
@FlowDefinition(id = "ReadFileIterate", runner = ReadFileIterateFlowRunner.class)
public class ReadTextFileIterateFlow extends IterateFlow {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String target;

    /**
     * ファイル種別.
     */
    private String fileType;

    /**
     *
     */
    private String encoding;

    /**
     *
     */
    private String lineSeparator;

    /**
     *
     */
    private String wrapstring;

}
