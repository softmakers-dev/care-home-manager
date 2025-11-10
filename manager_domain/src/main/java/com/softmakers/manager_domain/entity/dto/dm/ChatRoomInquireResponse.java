package com.softmakers.manager_domain.entity.dto.dm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomInquireResponse {

    private boolean status;
    private Long unseenCount;
}
