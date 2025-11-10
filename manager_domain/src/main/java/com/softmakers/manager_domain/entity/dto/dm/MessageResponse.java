package com.softmakers.manager_domain.entity.dto.dm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    private MessageAction action;
    private Object data;
}
