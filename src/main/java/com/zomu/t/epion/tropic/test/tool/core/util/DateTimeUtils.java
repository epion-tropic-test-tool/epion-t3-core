package com.zomu.t.epion.tropic.test.tool.core.util;

import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

/**
 * 日付時間関連のユーティリティ.
 *
 * @author takashno
 */
public final class DateTimeUtils {

    private static final DateTimeUtils instance = new DateTimeUtils();

    /**
     * 日時分秒ミリ秒までの標準フォーマット.
     */
    public static final DateTimeFormatter YYYYMMDD_HHMMSS_NORMAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static final DateTimeFormatter HHMMSS_NORMAL = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");


    public static DateTimeUtils getInstance() {
        return instance;
    }

    private DateTimeUtils() {
        // Do Nothing...
    }

    /**
     * @param start
     * @param end
     * @return
     */
    public long getMillis(Temporal start, Temporal end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.MILLIS.between(start, end);
    }

    public String formatNormal(Temporal target) {
        if (target == null) {
            return null;
        }
        return YYYYMMDD_HHMMSS_NORMAL.format(target);
    }

    public String formatTimeNormal(Temporal target) {
        if (target == null) {
            return null;
        }
        return HHMMSS_NORMAL.format(target);
    }

    public LocalDateTime referFlowStartDate(ExecuteScenario executeScenario, String flowId) {
        String startTimeKey = flowId + ExecuteScenario.FLOW_START_VARIABLE_SUFFIX;
        if (executeScenario.getScenarioVariables().containsKey(startTimeKey)) {
            Object startDateListObj = executeScenario.getScenarioVariables().get(startTimeKey);
            if (startDateListObj != null && List.class.isAssignableFrom(startDateListObj.getClass())) {
                List<LocalDateTime> startDateList = List.class.cast(startDateListObj);
                return startDateList.get(startDateList.size() - 1);
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0014, flowId);
            }
        } else {
            throw new SystemException(CoreMessages.CORE_ERR_0014, flowId);
        }
    }

    public LocalDateTime referFlowEndDate(ExecuteScenario executeScenario, String flowId) {
        String startTimeKey = flowId + ExecuteScenario.FLOW_END_VARIABLE_SUFFIX;
        if (executeScenario.getScenarioVariables().containsKey(startTimeKey)) {
            Object startDateListObj = executeScenario.getScenarioVariables().get(startTimeKey);
            if (startDateListObj != null && List.class.isAssignableFrom(startDateListObj.getClass())) {
                List<LocalDateTime> startDateList = List.class.cast(startDateListObj);
                return startDateList.get(startDateList.size() - 1);
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0015, flowId);
            }
        } else {
            throw new SystemException(CoreMessages.CORE_ERR_0015, flowId);
        }
    }


}
