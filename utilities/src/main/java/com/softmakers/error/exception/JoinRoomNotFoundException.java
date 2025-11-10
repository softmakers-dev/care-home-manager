package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class JoinRoomNotFoundException extends BusinessException {

    public JoinRoomNotFoundException() {
        super(ErrorCode.JOIN_ROOM_NOT_FOUND);
    }
}
