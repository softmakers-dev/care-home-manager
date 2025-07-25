package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class AccountMismatchException extends BusinessException {
    public AccountMismatchException() {
        super(ErrorCode.ACCOUNT_MISMATCH);
    }
}
