package com.softmakers.manager_domain.entity.feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PostLikeDto {

    private Long postId;
    private String username;

    @QueryProjection
    public PostLikeDto(Long postId, String username) {
        this.postId = postId;
        this.username = username;
    }
}
