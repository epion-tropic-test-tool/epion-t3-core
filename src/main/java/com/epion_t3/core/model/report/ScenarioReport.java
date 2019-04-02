package com.epion_t3.core.model.report;

import com.epion_t3.core.type.ScenarioExecuteStatus;
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
