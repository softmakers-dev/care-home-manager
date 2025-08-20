package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class NotSupportedImageTypeException extends BusinessException {
    public NotSupportedImageTypeException() {
        super(ErrorCode.IMAGE_TYPE_NOT_SUPPORTED);
    }
}
