package com.softmakers.manager_domain.entity.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDto {

    private Long boardId;
    private String boardName;
    private String description;
}
