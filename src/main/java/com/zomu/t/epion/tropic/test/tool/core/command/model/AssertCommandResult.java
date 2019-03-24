package com.zomu.t.epion.tropic.test.tool.core.command.model;

import com.zomu.t.epion.tropic.test.tool.core.type.AssertStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * アサート結果.
 *
 * @author takashno
 */
@Getter
@Setter
public class AssertCommandResult extends CommandResult {

    private Object expected;

    private Object actual;

    private AssertStatus assertStatus = AssertStatus.WAIT;

    public static AssertCommandResult getSuccess() {
        AssertCommandResult result = new AssertCommandResult();
        result.setAssertStatus(AssertStatus.OK);
        return result;
    }

}
