package com.epion_t3.core.common.util;

import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * エラー関連のユーティリティ.
 *
 * @author takashno
 */
@Slf4j
public final class ErrorUtils {

    /**
     * シングルトンインスタンス.
     */
    private static final ErrorUtils instance = new ErrorUtils();

    /**
     * プライベートコンストラクタ.
     */
    private ErrorUtils() {
    }

    /**
     * インスタンスを取得.
     *
     * @return
     */
    public static ErrorUtils getInstance() {
        return instance;
    }

    /**
     * スタックトレースを文字列で取得.
     *
     * @param t
     * @return
     */
    public String getStacktrace(Throwable t) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);) {
            t.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_0001, e);
        }
    }

}
