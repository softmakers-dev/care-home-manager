package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class EmailSendFailException extends BusinessException {

    public EmailSendFailException() {
        super(ErrorCode.EMAIL_SEND_FAIL);
    }

}
