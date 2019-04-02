package com.epion_t3.core.exception;

import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.Messages;

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
