package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class UserJpoDoesNotExistException extends BusinessException {
    public UserJpoDoesNotExistException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }

}
