package com.epion_t3.core.exception;

/**
 * メッセージが見つからない例外.
 *
 * @author takashno
 */
public class MessageNotFoundException extends SystemException {

    private String messageCode;

    public MessageNotFoundException(String messageCode) {
        super("'" + messageCode + "' is not found in message resource...");
    }


}
