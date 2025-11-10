package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class CantUploadReplyException extends BusinessException {

    public CantUploadReplyException() {
        super(ErrorCode.REPLY_CANT_UPLOAD);
    }

}
