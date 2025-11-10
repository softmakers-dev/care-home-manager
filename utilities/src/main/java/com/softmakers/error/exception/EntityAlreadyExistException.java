package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class EntityAlreadyExistException extends BusinessException {

    public EntityAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
