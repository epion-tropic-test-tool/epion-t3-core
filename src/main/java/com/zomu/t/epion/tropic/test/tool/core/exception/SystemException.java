package com.zomu.t.epion.tropic.test.tool.core.exception;

import com.zomu.t.epion.tropic.test.tool.core.message.Messages;
import com.zomu.t.epion.tropic.test.tool.core.message.MessageManager;

/**
 * 汎用システム例外.
 *
 * @author takashno
 */
public class SystemException extends RuntimeException {

    public SystemException(Throwable t, String messageCode) {
        super(MessageManager.getInstance().getMessage(messageCode), t);
    }

    public SystemException(Throwable t, Messages messages) {
        super(MessageManager.getInstance().getMessage(messages.getMessageCode()), t);
    }

    public SystemException(Throwable t, String messageCode, Object... objects) {
        super(MessageManager.getInstance().getMessage(messageCode, objects), t);
    }

    public SystemException(Throwable t, Messages messages, Object... objects) {
        super(MessageManager.getInstance().getMessage(messages.getMessageCode(), objects), t);
    }

    public SystemException(Throwable t) {
        super(t);
    }

    public SystemException(String messageCode) {
        super(MessageManager.getInstance().getMessageWithCode(messageCode));
    }

    public SystemException(Messages messages) {
        super(MessageManager.getInstance().getMessageWithCode(messages.getMessageCode()));
    }

    public SystemException(String messageCode, Object... objects) {
        super(MessageManager.getInstance().getMessageWithCode(messageCode, objects));
    }

    public SystemException(Messages messages, Object... objects) {
        super(MessageManager.getInstance().getMessageWithCode(messages.getMessageCode(), objects));
    }

}
