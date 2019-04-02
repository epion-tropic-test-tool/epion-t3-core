package com.epion_t3.core.exception;

import com.epion_t3.core.exception.bean.ScenarioParseError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * シナリオが解析できない例外.
 *
 * @author takashno
 */
public class ScenarioParseException extends RuntimeException {

    @Getter
    private List<ScenarioParseError> errors;

    public ScenarioParseException(List<ScenarioParseError> errors) {
        super("Scenario Parse Error Occurred.");
        this.errors = errors;
    }

    public ScenarioParseException(ScenarioParseError error) {
        super("Scenario Parse Error Occurred.");
        errors = new ArrayList<>();
        errors.add(error);
    }


}
