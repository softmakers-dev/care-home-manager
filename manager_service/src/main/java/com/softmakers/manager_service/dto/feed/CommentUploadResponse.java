package com.softmakers.manager_service.dto.feed;

import com.softmakers.manager_domain.entity.feed.CommentDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUploadResponse {

    private CommentDto comment;

}
