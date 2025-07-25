package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class FilterMustRespondException extends BusinessException{

    public FilterMustRespondException() {
        super(ErrorCode.FILTER_MUST_RESPOND);
    }
}
