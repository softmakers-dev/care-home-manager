package com.softmakers.error.exception;

import com.softmakers.error.ErrorCode;

public class ChatRoomNotFoundException extends BusinessException {

    public ChatRoomNotFoundException() {
        super(ErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
