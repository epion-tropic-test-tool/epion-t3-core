package com.epion_t3.core.exception.handler.impl;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.bean.Message;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.exception.ScenarioParseException;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.exception.handler.ExceptionHandler;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;

/**
 * @author takashno
 */
@Slf4j
public final class ExceptionHandlerImpl implements ExceptionHandler<Context, ExecuteContext> {

    /**
     * シングルトンインスタンス.
     */
    private static final ExceptionHandlerImpl instance = new ExceptionHandlerImpl();

    /**
     * プライベートコンストラクタ.
     */
    private ExceptionHandlerImpl() {
        // Do Nothing...
    }

    public static ExceptionHandlerImpl getInstance() {
        return instance;
    }

    @Override
    public void handle(final Context context, final ExecuteContext executeContext, final Throwable t) {

        MessageManager messageManager = MessageManager.getInstance();

        if (ScenarioParseException.class.isAssignableFrom(t.getClass())) {

            // シナリオ解析エラー
            log.error(messageManager.getMessage(CoreMessages.CORE_ERR_0040));

            for (ET3Notification notification : executeContext.getNotifications()) {

                switch (notification.getLevel()) {
                    case INFO:
                        log.info(notification.getMessage());
                        break;
                    case WARN:
                        if (context.getOption().getDebug() && notification.getError() != null) {
                            log.warn(notification.getMessage(), notification.getError());
                        } else {
                            log.warn(notification.getMessage());
                        }
                        break;
                    case ERROR:
                        if (context.getOption().getDebug()) {
                            log.error(notification.getMessage(), notification.getError());
                        } else {
                            log.error(notification.getMessage());
                        }
                        break;
                    default:
                        // Do Nothing...
                        break;
                }

            }

        }
        // システムエラー
        else if (SystemException.class.isAssignableFrom(t.getClass())) {
            SystemException se = SystemException.class.cast(t);
            log.error(se.getMessage(), se);
        }
        // 不明エラー
        else {

            t.printStackTrace();
        }


    }

}
