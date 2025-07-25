package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class FileConvertFailException extends BusinessException {

    public FileConvertFailException() {
        super(ErrorCode.FILE_CONVERT_FAIL);
    }

}
