package com.zomu.t.epion.tropic.test.tool.core.model.report;

import com.zomu.t.epion.tropic.test.tool.core.type.ScenarioExecuteStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@Setter
public class ScenarioReport implements Serializable {

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private ScenarioExecuteStatus result;

    private String message;

    private String note;

    private String error;

    private List<ProcessElement> processes = new ArrayList<>();


}
