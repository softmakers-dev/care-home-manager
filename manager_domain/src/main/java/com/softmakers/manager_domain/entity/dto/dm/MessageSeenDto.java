package com.softmakers.manager_domain.entity.dto.dm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSeenDto {

    private Long roomId;
    private Long memberId;
    private LocalDateTime seenDate;
}
