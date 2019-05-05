package com.epion_t3.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ID関連のユーティリティ.
 *
 * @author takashno
 */
@Slf4j
public final class IDUtils {

    /**
     * シングルトンインスタンス
     */
    private static final IDUtils instance = new IDUtils();

    /**
     * コマンドIDの結合子.
     */
    public static final String COMMAND_ID_JOINER = "@";

    /**
     * 設定IDの結合子.
     */
    public static final String CONFIGURATION_ID_JOINER = "@";

    /**
     * FullコマンドIDパターン.
     */
    private static final Pattern FULL_COMMAND_ID_PATTERN = Pattern.compile("([^@]+)@([^@]+)");

    /**
     * Full設定IDパターン.
     */
    private static final Pattern FULL_CONFIGURATION_ID_PATTERN = Pattern.compile("([^@]+)@([^@]+)");

    /**
     * プライベートコンストラクタ
     */
    private IDUtils() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return シングルトンインスタンス
     */
    public static IDUtils getInstance() {
        return instance;
    }

    /**
     * FullコマンドIDを作成する.
     *
     * @param fqsn      Fullシナリオ名
     * @param commandId コマンドID
     * @return FullコマンドID
     */
    public String createFullCommandId(String fqsn, String commandId) {
        return fqsn + COMMAND_ID_JOINER + commandId;
    }

    /**
     * Full設定IDを作成する.
     *
     * @param fqsn            Fullシナリオ名
     * @param configurationId コマンドID
     * @return Full設定ID
     */
    public String createFullConfigurationId(String fqsn, String configurationId) {
        return fqsn + CONFIGURATION_ID_JOINER + configurationId;
    }

    /**
     * FQCNから属するシナリオIDを取得.
     *
     * @param fullQueryCommandId FullコマンドID
     * @return シナリオID
     */
    public String extractBelongScenarioIdFromFqcn(String fullQueryCommandId) {
        if (StringUtils.isNotEmpty(fullQueryCommandId)) {
            Matcher m = FULL_COMMAND_ID_PATTERN.matcher(fullQueryCommandId);
            if (m.find()) {
                return m.group(1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Full設定IDから属するシナリオIDを取得.
     *
     * @param fullQueryConfigurationId Full設定ID
     * @return シナリオID
     */
    public String extractBelongScenarioIdFromFullConfigurationId(String fullQueryConfigurationId) {
        if (!StringUtils.isNotEmpty(fullQueryConfigurationId)) {
            Matcher m = FULL_CONFIGURATION_ID_PATTERN.matcher(fullQueryConfigurationId);
            if (m.find()) {
                return m.group(1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * FQCNであるか判定する.
     *
     * @param target 対象文字列
     * @return 判定結果
     */
    public Boolean isFullQueryCommandName(String target) {
        if (StringUtils.isNotEmpty(target)) {
            Matcher m = FULL_COMMAND_ID_PATTERN.matcher(target);
            return m.find();
        } else {
            return false;
        }
    }

    /**
     * Full設定IDであるか判定する.
     *
     * @param target 対象文字列
     * @return 判定結果
     */
    public Boolean isFullQueryConfigurationId(String target) {
        if (StringUtils.isNotEmpty(target)) {
            Matcher m = FULL_CONFIGURATION_ID_PATTERN.matcher(target);
            return m.find();
        } else {
            return false;
        }
    }

}
