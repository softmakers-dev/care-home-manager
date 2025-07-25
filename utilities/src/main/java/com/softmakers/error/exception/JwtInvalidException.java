package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class JwtInvalidException extends BusinessException{
    public JwtInvalidException() {
        super(ErrorCode.JWT_INVALID);
    }
}
