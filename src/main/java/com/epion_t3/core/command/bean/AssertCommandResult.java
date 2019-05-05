package com.epion_t3.core.command.bean;

import com.epion_t3.core.common.type.AssertStatus;
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
