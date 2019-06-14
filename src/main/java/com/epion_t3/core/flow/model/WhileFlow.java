package com.epion_t3.core.flow.model;

import com.epion_t3.core.common.annotation.FlowDefinition;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.flow.runner.impl.WhileFlowRunner;
import lombok.Getter;
import lombok.Setter;

/**
 * Whileを実現する制御Flow.
 *
 * @author takashno
 */
@Getter
@Setter
@FlowDefinition(id = "While", runner = WhileFlowRunner.class)
public class WhileFlow extends IterateFlow {

    /**
     * 式.
     */
    private String condition;

    /**
     * タイムアウト時間（ミリ秒）.
     */
    private Integer timeout;

    /**
     * タイムアウト時に後続Flowを継続するかどうか.
     */
    private Boolean continueFlow = false;

}
