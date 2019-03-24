package com.zomu.t.epion.tropic.test.tool.core.exception.handler;

import com.zomu.t.epion.tropic.test.tool.core.exception.ScenarioParseException;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.message.MessageManager;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;

/**
 * @author takashno
 */
@Slf4j
public final class BaseExceptionHandler implements ExceptionHandler<Context> {

    /**
     * シングルトンインスタンス.
     */
    private static final BaseExceptionHandler instance = new BaseExceptionHandler();

    /**
     * プライベートコンストラクタ.
     */
    private BaseExceptionHandler() {
        // Do Nothing...
    }

    public static BaseExceptionHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(final Context context, final Throwable t) {

        MessageManager messageManager = MessageManager.getInstance();

        t.printStackTrace();

        // シナリオ不正
        if (ScenarioParseException.class.isAssignableFrom(t.getClass())) {
            ScenarioParseException spe = ScenarioParseException.class.cast(t);
            spe.getErrors().forEach(x -> {
                String msg = null;
                if (x.getTarget() != null) {
                    if (x.getValue() != null) {
                        msg = messageManager.getMessage(CoreMessages.CORE_ERR_0010, x.getFilePath(), x.getMessage(), x.getTarget(), x.getValue());
                    } else {
                        msg = messageManager.getMessage(CoreMessages.CORE_ERR_0012, x.getFilePath(), x.getMessage(), x.getTarget());
                    }
                } else {
                    msg = messageManager.getMessage(CoreMessages.CORE_ERR_0011, x.getFilePath(), x.getMessage());
                }
                log.error(msg);
            });
        }

        // システムエラー
        if (SystemException.class.isAssignableFrom(t.getClass())) {
            SystemException se = SystemException.class.cast(t);
            log.error(se.getMessage());
        }


    }

}
