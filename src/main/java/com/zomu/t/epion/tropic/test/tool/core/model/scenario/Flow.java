package com.zomu.t.epion.tropic.test.tool.core.model.scenario;

import com.zomu.t.epion.tropic.test.tool.core.annotation.OriginalFlowField;
import lombok.Getter;
import lombok.Setter;
import org.apache.bval.constraints.NotEmpty;

import java.io.Serializable;

/**
 * Flow.
 *
 * @author takashno
 */
@Getter
@Setter
public class Flow implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @OriginalFlowField
    @NotEmpty
    private String id;

    /**
     * Flow種別.
     */
    @OriginalFlowField
    @NotEmpty
    private String type;

    @OriginalFlowField
    private String summary;

}
