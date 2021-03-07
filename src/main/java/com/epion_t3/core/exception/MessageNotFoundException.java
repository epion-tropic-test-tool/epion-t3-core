/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.exception;

import com.epion_t3.core.message.impl.CoreMessages;

/**
 * メッセージが見つからない例外.
 *
 * @author takashno
 */
public class MessageNotFoundException extends SystemException {

    private String messageCode;

    public MessageNotFoundException(String messageCode) {
        super(CoreMessages.CORE_ERR_0068, messageCode);
    }

}
