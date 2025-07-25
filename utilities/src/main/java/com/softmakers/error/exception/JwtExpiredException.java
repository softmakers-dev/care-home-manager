package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class JwtExpiredException extends BusinessException{
    public JwtExpiredException() {
        super(ErrorCode.JWT_EXPIRED);
    }
}
