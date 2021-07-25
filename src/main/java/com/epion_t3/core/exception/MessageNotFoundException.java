/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.exception;

import com.epion_t3.core.message.impl.CoreMessages;
import lombok.Getter;

/**
 * メッセージが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class MessageNotFoundException extends SystemException {

    /**
     * メッセージコード.
     */
    private String messageCode;

    /**
     * コンストラクタ.
     *
     * @param messageCode メッセージコード
     */
    public MessageNotFoundException(String messageCode) {
        super(CoreMessages.CORE_ERR_0068, messageCode);
        this.messageCode = messageCode;
    }

}
